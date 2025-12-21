// Config: reads/writes data/config.properties (stores pointsPerRupee)

package util;

import java.util.Properties;

public class Config {
    private static Config instance;
    private final DataStore ds = DataStore.getInstance();
    private final String file = "data/config.properties";
    private int pointsPerRupee = 10; // default 10 points = 1 rupee

    private Config() { load(); }

    public static synchronized Config getInstance() {
        if (instance == null) instance = new Config();
        return instance;
    }

    private synchronized void load() {
        Properties p = ds.readProperties(file);
        String s = p.getProperty("pointsPerRupee");
        if (s != null) {
            try { pointsPerRupee = Integer.parseInt(s.trim()); }
            catch (Exception ignored) {}
        } else {
            persist();
        }
    }

    public synchronized int getPointsPerRupee() { return pointsPerRupee; }

    public synchronized void setPointsPerRupee(int v) {
        if (v < 1) return;
        pointsPerRupee = v;
        persist();
    }

    private synchronized void persist() {
        Properties p = new Properties();
        p.setProperty("pointsPerRupee", String.valueOf(pointsPerRupee));
        ds.writeProperties(file, p);
    }
}
