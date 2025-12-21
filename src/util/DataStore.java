// DataStore: basic thread-safe CSV read/append/write utilities

package util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DataStore {
    private static DataStore instance;

    private DataStore() {
        try { Files.createDirectories(Paths.get("data")); }
        catch (IOException ignored) {}
        // ensure files exist
        try { Files.createFile(Paths.get("data/users.csv")); } catch (IOException ignored) {}
        try { Files.createFile(Paths.get("data/products.csv")); } catch (IOException ignored) {}
        try { Files.createFile(Paths.get("data/orders.csv")); } catch (IOException ignored) {}
        try { Files.createFile(Paths.get("data/stats.csv")); } catch (IOException ignored) {}
        try { Files.createFile(Paths.get("data/config.properties")); } catch (IOException ignored) {}
        try { Files.createFile(Paths.get("data/logs.txt")); } catch (IOException ignored) {}
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    public synchronized List<String> readCSV(String path) {
        try {
            Path p = Paths.get(path);
            if (!Files.exists(p)) return new ArrayList<>();
            return Files.readAllLines(p);
        } catch (IOException e) { return new ArrayList<>(); }
    }

    public synchronized void appendCSV(String path, String line) {
        try {
            Files.write(Paths.get(path), Arrays.asList(line), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }

    public synchronized void writeAll(String path, List<String> lines) {
        try {
            Files.write(Paths.get(path), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ignored) {}
    }

    public synchronized Properties readProperties(String path) {
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get(path))) { p.load(in); }
        catch (IOException ignored) {}
        return p;
    }

    public synchronized void writeProperties(String path, Properties p) {
        try (OutputStream out = Files.newOutputStream(Paths.get(path))) { p.store(out, "config"); }
        catch (IOException ignored) {}
    }
}
