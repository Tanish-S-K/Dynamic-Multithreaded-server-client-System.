// Basket: holds simple productId -> quantity mapping for one customer's session

package model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Basket {
    private final Map<String, Double> items = new LinkedHashMap<>(); // id -> qty

    public synchronized void addItem(String productId, double qty) {
        items.put(productId, items.getOrDefault(productId, 0.0) + qty);
    }

    public synchronized void removeItem(String productId) {
        items.remove(productId);
    }

    public synchronized Map<String, Double> getItems() {
        return new LinkedHashMap<>(items);
    }

    public synchronized boolean isEmpty() { return items.isEmpty(); }

    public synchronized void clear() { items.clear(); }
}
