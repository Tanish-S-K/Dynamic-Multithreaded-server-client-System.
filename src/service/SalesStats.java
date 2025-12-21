// SalesStats: tracks total sold per product and persists to data/stats.csv

package service;

import util.DataStore;
import java.util.*;

public class SalesStats {
    private final Map<String, Integer> counts = new HashMap<>();
    private final DataStore ds = DataStore.getInstance();
    private final String file = "data/stats.csv";

    public SalesStats() { load(); }

    private synchronized void load() {
        List<String> lines = ds.readCSV(file);
        for (String l : lines) {
            String[] a = l.split(",", -1);
            if (a.length >= 2) {
                try { counts.put(a[0], Integer.parseInt(a[1])); }
                catch (Exception ignored) {}
            }
        }
    }

    public synchronized void increment(String productId, int qty) {
        counts.put(productId, counts.getOrDefault(productId, 0) + qty);
        persist();
    }

    private synchronized void persist() {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> e : counts.entrySet()) lines.add(e.getKey() + "," + e.getValue());
        ds.writeAll(file, lines);
    }

    public synchronized List<Map.Entry<String,Integer>> topN(int n) {
        List<Map.Entry<String,Integer>> list = new ArrayList<>(counts.entrySet());
        list.sort((a,b) -> Integer.compare(b.getValue(), a.getValue()));
        return list.subList(0, Math.min(n, list.size()));
    }
}
