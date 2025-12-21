// User model: username, password and persistent point balance

package user;

public class User {
    private final String username;
    private final String password;
    private int points;

    public User(String username, String password, int points) {
        this.username = username;
        this.password = password;
        this.points = points;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public synchronized int getPoints() { return points; }
    public synchronized void setPoints(int p) { points = p; }
    public synchronized void addPoints(int p) { points += p; }
    public synchronized boolean deductPoints(int p) {
        if (points < p) return false;
        points -= p;
        return true;
    }

    @Override
    public String toString() { return username + " (pts:" + points + ")"; }
}
