// GroceryServer: starts listening socket and spawns ClientHandler threads

package server;

import service.Inventory;
import service.SalesStats;
import model.OrderHistory;
import user.UserManager;
import util.Logger;

import java.net.ServerSocket;
import java.net.Socket;

public class GroceryServer {
    public static final int PORT = 5000;

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        OrderHistory orderHistory = new OrderHistory();
        UserManager userManager = new UserManager();
        SalesStats salesStats = new SalesStats();
        Logger logger = Logger.getInstance();

        logger.log("Starting GroceryServer on port " + PORT);
        try (ServerSocket ss = new ServerSocket(PORT)) {
            while (true) {
                Socket s = ss.accept();
                ClientHandler ch = new ClientHandler(s, inventory, orderHistory, userManager, salesStats, logger);
                ch.start();
            }
        } catch (Exception e) {
            logger.log("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
