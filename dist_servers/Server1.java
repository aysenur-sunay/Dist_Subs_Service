package servers;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server1 {
    private static AtomicInteger capacity = new AtomicInteger(100);

    public static void main(String[] args) {
        connectToServers("localhost", 5002); 
        connectToServers("localhost", 5003); 

        try (ServerSocket serverSocket = new ServerSocket(5001)) {
            System.out.println("Server1 running on port 5001...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void connectToServers(String host, int port) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("Connected to server on port " + port);
            out.println("Hello from Server1");
        } catch (IOException e) {
            System.out.println("Failed to connect to server on port " + port);
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String message = in.readLine();
                System.out.println("Received: " + message);
                out.println("Capacity: " + capacity.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
