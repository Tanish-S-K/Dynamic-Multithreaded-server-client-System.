// Product model: holds id, name, category, money price, points price (optional), and reward points earned on money purchase

package model;

public class Product {
    private final String id;
    private String name;
    private Category category;
    private double priceMoney;     // money price (e.g., rupees)
    private int pricePoints;       // optional points price (0 = compute via config)
    private int rewardPoints;      // points earned per unit (e.g., per kg)

    public Product(String id, String name, Category category, double priceMoney, int pricePoints, int rewardPoints) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.priceMoney = priceMoney;
        this.pricePoints = pricePoints;
        this.rewardPoints = rewardPoints;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public double getPriceMoney() { return priceMoney; }
    public int getPricePoints() { return pricePoints; }
    public int getRewardPoints() { return rewardPoints; }

    public void setName(String n) { this.name = n; }
    public void setCategory(Category c) { this.category = c; }
    public void setPriceMoney(double p) { this.priceMoney = p; }
    public void setPricePoints(int pts) { this.pricePoints = pts; }
    public void setRewardPoints(int pts) { this.rewardPoints = pts; }

    public String toCSV() {
        return String.join(",", id, name, category.toString(), String.valueOf(priceMoney),
                String.valueOf(pricePoints), String.valueOf(rewardPoints));
    }

    public static Product fromCSV(String line) {
        try {
            String[] a = line.split(",", -1);
            if (a.length < 6) return null;
            String id = a[0];
            String name = a[1];
            Category cat = Category.fromString(a[2]);
            double pm = Double.parseDouble(a[3]);
            int pp = Integer.parseInt(a[4]);
            int rp = Integer.parseInt(a[5]);
            return new Product(id, name, cat, pm, pp, rp);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s | %-15s | %s | ₹%.2f | ptsPrice:%d | earnPts:%d",
                id, name, category, priceMoney, pricePoints, rewardPoints);
    }
}
