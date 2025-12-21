// ClientHandler: handles a single client session (login, menus, checkout, points)

package server;

import service.Inventory;
import service.SalesStats;
import model.*;
import model.Order;
import model.OrderHistory;
import user.UserManager;
import util.Config;
import util.Logger;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final Inventory inventory;
    private final OrderHistory orderHistory;
    private final UserManager userManager;
    private final SalesStats salesStats;
    private final Logger logger;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, Inventory inventory, OrderHistory orderHistory,
                         UserManager userManager, SalesStats salesStats, Logger logger) {
        this.socket = socket;
        this.inventory = inventory;
        this.orderHistory = orderHistory;
        this.userManager = userManager;
        this.salesStats = salesStats;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            logger.log("[CONNECT] " + socket.getInetAddress());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("=== Grocery Server ===");
            out.println("Enter username:");
            username = safeRead();
            out.println("Enter password:");
            String pass = safeRead();

            if (!userManager.login(username, pass)) {
                out.println("Invalid credentials. Closing.");
                logger.log("[AUTH FAIL] " + username + " from " + socket.getInetAddress());
                socket.close();
                return;
            }

            logger.log("[LOGIN] " + username + " from " + socket.getInetAddress());
            out.println("Login successful. Hello, " + username + "!");

            if (userManager.isAdmin(username)) adminMenu();
            else consumerMenu();

            socket.close();
            logger.log("[DISCONNECT] " + username + " disconnected.");
        } catch (Exception e) {
            logger.log("[ERROR] client handler: " + e.getMessage());
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private String safeRead() throws IOException {
        String s = in.readLine();
        return s == null ? "" : s.trim();
    }

    // Admin menu with ability to change pointsPerRupee and view stats
    private void adminMenu() throws IOException {
        while (true) {
            out.println("\n--- Admin Menu ---");
            out.println("1. Add Product");
            out.println("2. Update Product");
            out.println("3. Remove Product");
            out.println("4. List Products");
            out.println("5. View Sales Stats");
            out.println("6. View Orders CSV (raw)");
            out.println("7. Register User");
            out.println("8. Set pointsPerRupee (current: " + Config.getInstance().getPointsPerRupee() + ")");
            out.println("0. Logout");
            out.println("Enter choice:");
            String ch = safeRead();
            switch (ch) {
                case "1":
                    out.println("Enter id:");
                    String id = safeRead();
                    out.println("Enter name:");
                    String name = safeRead();
                    out.println("Enter category:");
                    Category cat = Category.fromString(safeRead());
                    out.println("Enter money price (₹):");
                    double price = parseDoubleSafe(safeRead(), 0.0);
                    out.println("Enter points price (pts) (0 to calculate by config):");
                    int pp = parseIntSafe(safeRead(), 0);
                    out.println("Enter reward points earned per unit:");
                    int rp = parseIntSafe(safeRead(), 0);
                    Product p = new Product(id, name, cat, price, pp, rp);
                    inventory.addProduct(p);
                    out.println("Product added.");
                    logger.log("[ADMIN] " + username + " added product " + id);
                    break;
                case "2":
                    out.println("Enter id to update:");
                    String uid = safeRead();
                    Product up = inventory.getById(uid);
                    if (up == null) { out.println("Not found."); break; }
                    out.println("Enter new name (blank to keep):"); String nn = safeRead(); if (!nn.isEmpty()) up.setName(nn);
                    out.println("Enter new category (blank to keep):"); String nc = safeRead(); if (!nc.isEmpty()) up.setCategory(Category.fromString(nc));
                    out.println("Enter new money price (blank to keep):"); String np = safeRead(); if (!np.isEmpty()) up.setPriceMoney(Double.parseDouble(np));
                    out.println("Enter new points price (blank to keep):"); String npp = safeRead(); if (!npp.isEmpty()) up.setPricePoints(Integer.parseInt(npp));
                    out.println("Enter new reward points (blank to keep):"); String nrp = safeRead(); if (!nrp.isEmpty()) up.setRewardPoints(Integer.parseInt(nrp));
                    inventory.updateProduct(up);
                    out.println("Updated.");
                    logger.log("[ADMIN] " + username + " updated product " + uid);
                    break;
                case "3":
                    out.println("Enter id to remove:"); String rid = safeRead(); inventory.removeProduct(rid); out.println("Removed if existed.");
                    logger.log("[ADMIN] " + username + " removed product " + rid);
                    break;
                case "4":
                    out.println("=== Product List ===");
                    for (Product prod : inventory.getAllProducts()) out.println(prod.toString());
                    break;
                case "5":
                    out.println("=== Top sold products ===");
                    List<Map.Entry<String,Integer>> top = salesStats.topN(10);
                    for (Map.Entry<String,Integer> e : top) out.println(e.getKey() + " sold: " + e.getValue());
                    break;
                case "6":
                    out.println("=== orders.csv ===");
                    List<String> raw = util.DataStore.getInstance().readCSV("data/orders.csv");
                    for (String rl : raw) out.println(rl);
                    break;
                case "7":
                    out.println("Enter new username:"); String nu = safeRead(); out.println("Enter password:"); String npass = safeRead();
                    boolean ok = userManager.registerUser(nu, npass);
                    out.println(ok ? "Registered." : "Failed (exists).");
                    if (ok) logger.log("[ADMIN] " + username + " registered user " + nu);
                    break;
                case "8":
                    out.println("Enter integer pointsPerRupee (e.g., 10 means 10 pts = ₹1):");
                    int v = parseIntSafe(safeRead(), Config.getInstance().getPointsPerRupee());
                    Config.getInstance().setPointsPerRupee(v);
                    out.println("Updated pointsPerRupee to " + v);
                    logger.log("[CONFIG] pointsPerRupee set to " + v + " by admin " + username);
                    break;
                case "0":
                    out.println("Logging out.");
                    return;
                default:
                    out.println("Invalid choice.");
            }
        }
    }

    private double parseDoubleSafe(String s, double def) {
        try { return Double.parseDouble(s); } catch(Exception e) { return def; }
    }
    private int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s); } catch(Exception e) { return def; }
    }

    // Consumer menu with buy options: money, points, mixed; supports "buy my usual"
    private void consumerMenu() throws IOException {
        Basket basket = new Basket();
        while (true) {
            out.println("\n--- Consumer Menu ---");
            out.println("1. List Products");
            out.println("2. Add to Basket");
            out.println("3. Remove from Basket");
            out.println("4. Checkout");
            out.println("5. View My Orders");
            out.println("6. Buy My Usual (last order)");
            out.println("7. View My Points");
            out.println("0. Logout");
            out.println("Enter choice:");
            String ch = safeRead();
            switch (ch) {
                case "1":
                    for (Product prod : inventory.getAllProducts()) out.println(prod.toString());
                    break;
                case "2":
                    out.println("Enter product id:"); String pid = safeRead();
                    out.println("Enter qty (kg):"); double qty = parseDoubleSafe(safeRead(), 1.0);
                    basket.addItem(pid, qty);
                    out.println("Added to basket.");
                    break;
                case "3":
                    out.println("Enter product id to remove:"); String rid = safeRead(); basket.removeItem(rid); out.println("Removed.");
                    break;
                case "4":
                    if (basket.isEmpty()) { out.println("Basket empty."); break; }
                    handleCheckout(basket);
                    basket.clear();
                    break;
                case "5":
                    List<Order> my = orderHistory.getOrdersForUser(username);
                    if (my.isEmpty()) out.println("No orders yet.");
                    else for (Order o : my) out.println(o.toString());
                    break;
                case "6":
                    Order last = orderHistory.getLastOrderForUser(username);
                    if (last == null) { out.println("No usual order found."); break; }
                    // immediate checkout of last items
                    Basket tmp = new Basket();
                    for (Map.Entry<String, Double> e : last.getItems().entrySet()) tmp.addItem(e.getKey(), e.getValue());
                    handleCheckout(tmp);
                    break;
                case "7":
                    out.println("Your points: " + userManager.getPoints(username));
                    break;
                case "0":
                    out.println("Logging out.");
                    return;
                default:
                    out.println("Invalid.");
            }
        }
    }

    // Core checkout flow: compute totals, allow payment options, apply discounts, update points & stats
    private void handleCheckout(Basket basket) throws IOException {
        Map<String, Double> items = basket.getItems();
        double moneyTotal = 0.0;
        int requiredPointsIfConverted = 0;
        StringBuilder bill = new StringBuilder("\n--- Checkout ---\n");
        for (Map.Entry<String, Double> e : items.entrySet()) {
            Product p = inventory.getById(e.getKey());
            double qty = e.getValue();
            if (p == null) { bill.append(e.getKey()).append(": product not found\n"); continue; }
            double lineMoney = p.getPriceMoney() * qty;
            moneyTotal += lineMoney;
            // if product has explicit points price use that * qty else convert money price
            int linePoints = p.getPricePoints() > 0 ? p.getPricePoints() * (int)Math.round(qty)
                    : (int)Math.ceil(p.getPriceMoney() * qty * Config.getInstance().getPointsPerRupee());
            requiredPointsIfConverted += linePoints;
            bill.append(String.format("%s x %.2f kg -> ₹%.2f or %d pts (earn %d pts)\n",
                    p.getName(), qty, lineMoney, linePoints, (int)Math.round(p.getRewardPoints() * qty)));
        }

        // apply discount by user's current points (discount applies to money portion)
        int userPts = userManager.getPoints(username);
        double discountRate = computeDiscountRate(userPts);
        double discountAmount = moneyTotal * discountRate;
        double moneyAfterDiscount = moneyTotal - discountAmount;

        bill.append(String.format("Subtotal: ₹%.2f\nDiscount: %.0f%% (-₹%.2f)\n", moneyTotal, discountRate*100, discountAmount));
        bill.append(String.format("Amount after discount: ₹%.2f\n", moneyAfterDiscount));
        bill.append("You can pay with: (1) Money only  (2) Points only  (3) Mixed points+money\n");
        bill.append(String.format("If fully by points (converted) requires: %d pts\n", requiredPointsIfConverted));
        out.println(bill.toString());
        out.println("Choose payment option (1/2/3):");
        String opt = safeRead();

        int earnedPoints = 0;
        double moneyPaid = 0.0;
        int pointsUsed = 0;

        if ("2".equals(opt)) {
            // points only
            if (userPts >= requiredPointsIfConverted) {
                userManager.deductPoints(username, requiredPointsIfConverted);
                pointsUsed = requiredPointsIfConverted;
                moneyPaid = 0.0;
                earnedPoints = 0; // no earning on points purchase
                out.println("Paid fully with points.");
            } else {
                out.println("Insufficient points. Transaction cancelled.");
                return;
            }
        } else if ("3".equals(opt)) {
            // mixed: ask how many points to use (or try to use all)
            out.println("You have " + userPts + " pts. Enter points to use (0 to cancel):");
            int use = parseIntSafe(safeRead(), 0);
            if (use <= 0) { out.println("Cancelled."); return; }
            if (use > userPts) use = userPts;
            double rupeeFromPoints = (double)use / Config.getInstance().getPointsPerRupee();
            double remainingMoney = moneyAfterDiscount - rupeeFromPoints;
            if (remainingMoney < 0) remainingMoney = 0;
            // deduct points
            userManager.deductPoints(username, use);
            pointsUsed = use;
            moneyPaid = remainingMoney;
            // earned points based on moneyPaid and product's reward points proportionally
            // We'll compute earnedPoints as sum of product.rewardPoints * qty * (moneyPaid / moneyAfterDiscount)
            double ratio = moneyAfterDiscount > 0 ? (moneyPaid / moneyAfterDiscount) : 0.0;
            for (Map.Entry<String, Double> e : items.entrySet()) {
                Product p = inventory.getById(e.getKey());
                if (p != null) earnedPoints += (int)Math.round(p.getRewardPoints() * e.getValue() * ratio);
            }
            if (earnedPoints > 0) userManager.addPointsToUser(username, earnedPoints);
            out.println(String.format("Used %d pts (%.2f ₹). Paid ₹%.2f in cash. Earned %d pts.", pointsUsed, rupeeFromPoints, moneyPaid, earnedPoints));
        } else {
            // money only
            moneyPaid = moneyAfterDiscount;
            // earn points per product fully
            for (Map.Entry<String, Double> e : items.entrySet()) {
                Product p = inventory.getById(e.getKey());
                if (p != null) earnedPoints += (int)Math.round(p.getRewardPoints() * e.getValue());
            }
            if (earnedPoints > 0) userManager.addPointsToUser(username, earnedPoints);
            out.println(String.format("Paid ₹%.2f in cash. Earned %d pts.", moneyPaid, earnedPoints));
        }

        // record order, update stats, log on server console
        String orderId = "ORD" + System.currentTimeMillis();
        Order order = new Order(orderId, username, items);
        orderHistory.addOrder(order);

        // increment stats counts (approx using qty rounded)
        for (Map.Entry<String, Double> e : items.entrySet()) {
            int qtyInt = Math.max(1, (int)Math.round(e.getValue()));
            salesStats.increment(e.getKey(), qtyInt);
        }

        logger.log(String.format("[ORDER] %s placed %s | cash:₹%.2f | ptsUsed:%d | ptsEarned:%d",
                username, orderId, moneyPaid, pointsUsed, earnedPoints));
        out.println("Order placed. Order ID: " + orderId);
    }

    private double computeDiscountRate(int pts) {
        if (pts >= 1000) return 0.20;
        if (pts >= 500) return 0.10;
        if (pts >= 100) return 0.05;
        return 0.0;
    }
}
