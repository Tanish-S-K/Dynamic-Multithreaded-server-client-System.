// OrderHistory: loads and appends orders to data/orders.csv

package model;

import util.DataStore;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderHistory {
    private final List<Order> orders = new ArrayList<>();
    private final DataStore ds = DataStore.getInstance();
    private final String file = "data/orders.csv";

    public OrderHistory() { load(); }

    private synchronized void load() {
        List<String> lines = ds.readCSV(file);
        for (String l : lines) {
            Order o = Order.fromCSVLine(l);
            if (o != null) orders.add(o);
        }
    }

    public synchronized void addOrder(Order o) {
        orders.add(o);
        ds.appendCSV(file, o.toCSVLine());
    }

    public synchronized List<Order> getOrdersForUser(String username) {
        return orders.stream().filter(x -> x.getUsername().equals(username)).collect(Collectors.toList());
    }

    public synchronized Order getLastOrderForUser(String username) {
        List<Order> lst = getOrdersForUser(username);
        return lst.isEmpty() ? null : lst.get(lst.size() - 1);
    }

    public synchronized List<Order> getAllOrders() { return new ArrayList<>(orders); }
}
