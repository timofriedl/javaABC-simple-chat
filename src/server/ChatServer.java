package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private ServerSocket serverSocket;

    private List<ClientHandler> clients;

    public ChatServer(int port) {
        clients = new CopyOnWriteArrayList<>();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started chat server on port " + port);

            while (true) {
                System.out.println("Waiting for new client...");
                Socket connectionToClient = serverSocket.accept();
                ClientHandler client = new ClientHandler(this, connectionToClient);
                clients.add(client);
                System.out.println("Accepted new client");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected");
    }

    public void broadcastMessage(String message) {
        System.out.println(message);
        if (message != null) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer(3141);
    }
}