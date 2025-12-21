// Order: represents a placed order and serializes to/from CSV

package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringJoiner;

public class Order {
    private final String orderId;
    private final String username;
    private final Map<String, Double> items; // productId -> qty
    private final long timestamp;

    public Order(String orderId, String username, Map<String, Double> items) {
        this.orderId = orderId;
        this.username = username;
        this.items = items;
        this.timestamp = System.currentTimeMillis();
    }

    public String getOrderId() { return orderId; }
    public String getUsername() { return username; }
    public Map<String, Double> getItems() { return items; }

    public String toCSVLine() {
        StringJoiner sj = new StringJoiner("|");
        for (Map.Entry<String, Double> e : items.entrySet()) sj.add(e.getKey() + ":" + e.getValue());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.join(",", orderId, username, sj.toString(), sdf.format(new Date(timestamp)));
    }

    public static Order fromCSVLine(String line) {
        try {
            String[] parts = line.split(",", 4);
            if (parts.length < 4) return null;
            String id = parts[0];
            String user = parts[1];
            String itemsPart = parts[2];
            Map<String, Double> items = new java.util.LinkedHashMap<>();
            if (!itemsPart.isEmpty()) {
                for (String kv : itemsPart.split("\\|")) {
                    String[] a = kv.split(":");
                    items.put(a[0], Double.parseDouble(a[1]));
                }
            }
            return new Order(id, user, items);
        } catch (Exception e) { return null; }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Order " + orderId + " by " + username + "\n");
        for (Map.Entry<String, Double> e : items.entrySet()) {
            sb.append(String.format("  %s x %.2f kg\n", e.getKey(), e.getValue()));
        }
        return sb.toString();
    }
}
