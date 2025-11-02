import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ElevatorClient {
    public static void main(String[] args) {
        final String LOCAL_HOST = "localhost";
        int PORT = 9000;
        try (Socket socket = new Socket(LOCAL_HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println(in.readLine());

            Thread listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            listener.start();

            while (true) {
                System.out.print("Enter floor (1–10) or 'exit': ");
                String input = scanner.nextLine();
                out.println(input);
                if (input.equalsIgnoreCase("exit")) break;
            }

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}
