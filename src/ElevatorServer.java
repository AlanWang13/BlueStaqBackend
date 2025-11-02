import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ElevatorServer {
    private final int PORT = 9000;
    private final int minFloor = 1;
    private final int maxFloor = 10;
    private int currentFloor = 1;
    private boolean movingUp = true;
    private final NavigableSet<Integer> requests = new TreeSet<>();
    private final Set<PrintWriter> clients = ConcurrentHashMap.newKeySet();
    private boolean running = true;

    public static void main(String[] args) {
        new ElevatorServer().startServer();
    }

    public void startServer() {
        System.out.println("Server started on port: "+ PORT);
        ExecutorService clientPool = Executors.newCachedThreadPool();

        new Thread(this::processRequests).start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (running) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());
                clientPool.submit(new ClientHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        clientPool.shutdown();
    }

    private void processRequests() {
        while (running) {
            Integer nextFloor;
            synchronized (requests) {
                if (movingUp) {
                    nextFloor = requests.ceiling(currentFloor);
                    if (nextFloor == null) {
                        movingUp = false;
                        continue;
                    }
                } else {
                    nextFloor = requests.floor(currentFloor);
                    if (nextFloor == null) {
                        movingUp = true;
                        continue;
                    }
                }
                requests.remove(nextFloor);
            }
            moveToFloor(nextFloor);
        }
    }

    private void moveToFloor(int targetFloor){
        broadcast("\n Elevator moving from floor " + currentFloor + " to " + targetFloor);
        try{
            while (currentFloor != targetFloor) {
                if (currentFloor < targetFloor) currentFloor++;
                else currentFloor--;
                broadcast("Elevator at floor " + currentFloor);
                Thread.sleep(1000);
                
                synchronized (requests) {
                    if (requests.contains(currentFloor)) {
                        requests.remove(currentFloor);
                        broadcast("Arrived at floor " + currentFloor);
                    }
                }
            }
            broadcast("Arrived at floor " + currentFloor);
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    private void broadcast(String msg) {
        System.out.println(msg);
        for (PrintWriter client : clients) {
            client.println(msg);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                clients.add(out);
                out.println("Connected to Elevator Server. Enter floor (1–10) or 'exit'.");

                String input;
                while ((input = in.readLine()) != null) {
                    if (input.equalsIgnoreCase("exit")) break;

                    try {
                        int floor = Integer.parseInt(input);
                        if (floor >= minFloor && floor <= maxFloor) {
                            synchronized (requests) {
                                requests.add(floor);
                            }
                            broadcast("New request: floor " + floor);
                        } else {
                            out.println("Invalid floor: " + floor);
                        }
                    } catch (NumberFormatException e) {
                        out.println("Invalid input. Enter a number (1–10) or 'exit'.");
                    }
                }

                clients.remove(out);
                socket.close();
                System.out.println("Client disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
