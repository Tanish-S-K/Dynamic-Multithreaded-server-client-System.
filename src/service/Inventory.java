// Inventory: manages products stored in data/products.csv

package service;

import model.Product;
import util.DataStore;
import java.util.*;

public class Inventory {
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final DataStore ds = DataStore.getInstance();
    private final String file = "data/products.csv";

    public Inventory() { load(); }

    private synchronized void load() {
        List<String> lines = ds.readCSV(file);
        for (String l : lines) {
            Product p = Product.fromCSV(l);
            if (p != null) products.put(p.getId(), p);
        }
    }

    public synchronized Collection<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public synchronized Product getById(String id) {
        return products.get(id);
    }

    public synchronized void addProduct(Product p) {
        products.put(p.getId(), p);
        ds.appendCSV(file, p.toCSV());
    }

    public synchronized void updateProduct(Product p) {
        products.put(p.getId(), p);
        persist();
    }

    public synchronized void removeProduct(String id) {
        products.remove(id);
        persist();
    }

    private synchronized void persist() {
        List<String> lines = new ArrayList<>();
        for (Product p : products.values()) lines.add(p.toCSV());
        ds.writeAll(file, lines);
    }
}
