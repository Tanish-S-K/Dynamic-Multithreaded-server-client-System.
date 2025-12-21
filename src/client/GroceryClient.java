// GroceryClient: simple console client that connects to server and proxies input/output

package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GroceryClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            Thread reader = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ignored) {}
            });
            reader.setDaemon(true);
            reader.start();

            while (true) {
                String input = sc.nextLine();
                out.println(input);
                if (input.equalsIgnoreCase("0") || input.equalsIgnoreCase("exit")) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
