package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private ChatServer chatServer;
    private Socket connectionToClient;

    private String name;

    private BufferedReader fromClientReader;
    private PrintWriter toClientWriter;

    public ClientHandler(ChatServer chatServer, Socket connectionToClient) {
        this.chatServer = chatServer;
        this.connectionToClient = connectionToClient;

        name = connectionToClient.getInetAddress().getHostAddress();

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            fromClientReader = new BufferedReader(new InputStreamReader(connectionToClient.getInputStream()));
            toClientWriter = new PrintWriter(new OutputStreamWriter(connectionToClient.getOutputStream()));

            chatServer.broadcastMessage(name + " connected.");
            while (true) {
                String message = fromClientReader.readLine();
                chatServer.broadcastMessage(name + ": " + message);
            }
        } catch (IOException e) {
            chatServer.removeClient(this);
            chatServer.broadcastMessage(name + " disconnected.");
        } finally {
            if (fromClientReader != null) {
                try {
                    fromClientReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (toClientWriter != null) {
                toClientWriter.close();
            }
        }

    }

    public void sendMessage(String message) {
        toClientWriter.println(message);
        toClientWriter.flush();
    }
}
