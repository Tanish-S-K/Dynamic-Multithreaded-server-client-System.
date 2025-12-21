// Logger: simple file+console logger writing to data/logs.txt

package util;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static Logger instance;
    private final PrintWriter pw;

    private Logger() {
        try {
            Files.createDirectories(Paths.get("data"));
            pw = new PrintWriter(new FileWriter("data/logs.txt", true), true);
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    public static synchronized Logger getInstance() {
        if (instance == null) instance = new Logger();
        return instance;
    }

    public synchronized void log(String msg) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = ts + " - " + msg;
        pw.println(line);
        System.out.println(line); // also print on server console
    }
}
