// UserManager: loads/saves users from data/users.csv and manages points

package user;

import util.DataStore;
import java.util.*;

public class UserManager {
    private final Map<String, User> users = new HashMap<>();
    private final DataStore ds = DataStore.getInstance();
    private final String file = "data/users.csv";

    public UserManager() { load(); ensureDefaultAdmin(); }

    private synchronized void load() {
        List<String> lines = ds.readCSV(file);
        for (String l : lines) {
            String[] a = l.split(",", -1);
            if (a.length >= 2) {
                String u = a[0], p = a[1];
                int pts = 0;
                if (a.length >= 3) {
                    try { pts = Integer.parseInt(a[2]); } catch(Exception ignored) {}
                }
                users.put(u, new User(u, p, pts));
            }
        }
    }

    private synchronized void ensureDefaultAdmin() {
        if (!users.containsKey("admin")) {
            users.put("admin", new User("admin", "admin123", 0));
            ds.appendCSV(file, "admin,admin123,0");
        }
    }

    public synchronized boolean login(String username, String password) {
        User u = users.get(username);
        return u != null && u.getPassword().equals(password);
    }

    public synchronized boolean isAdmin(String username) {
        return "admin".equals(username);
    }

    public synchronized boolean registerUser(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, password, 0));
        ds.appendCSV(file, username + "," + password + ",0");
        return true;
    }

    public synchronized int getPoints(String username) {
        User u = users.get(username);
        return u == null ? 0 : u.getPoints();
    }

    public synchronized void addPointsToUser(String username, int pts) {
        User u = users.get(username);
        if (u != null) {
            u.addPoints(pts);
            saveAll();
        }
    }

    public synchronized boolean deductPoints(String username, int pts) {
        User u = users.get(username);
        if (u == null) return false;
        boolean ok = u.deductPoints(pts);
        if (ok) saveAll();
        return ok;
    }

    public synchronized void setPoints(String username, int pts) {
        User u = users.get(username);
        if (u != null) {
            u.setPoints(pts);
            saveAll();
        }
    }

    private synchronized void saveAll() {
        List<String> lines = new ArrayList<>();
        for (User u : users.values()) lines.add(u.getUsername() + "," + u.getPassword() + "," + u.getPoints());
        ds.writeAll(file, lines);
    }

    // for admin listing (optional)
    public synchronized Collection<User> allUsers() { return new ArrayList<>(users.values()); }
}
