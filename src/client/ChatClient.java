package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

public class ChatClient extends JFrame implements KeyListener {
    private String address;
    private int port;

    // Connection
    private Socket connectionToServer;
    private BufferedReader fromServerReader;
    private PrintWriter toServerWriter;

    // GUI
    private JTextArea outputTextArea;
    private JTextField inputTextField;
    private JScrollPane outputScrollPane;

    public ChatClient(int port) {
        super("Chat");
        this.port = port;

        address = JOptionPane.showInputDialog("IP-Adresse");

        if (address != null) {
            receiveMessages();
        }
    }

    private void initGui() {
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setBorder(BorderFactory.createTitledBorder("Chat"));

        outputScrollPane = new JScrollPane(outputTextArea);

        inputTextField = new JTextField();
        inputTextField.setBorder(BorderFactory.createTitledBorder("Nachricht eingeben"));
        inputTextField.addKeyListener(this);

        add(outputScrollPane, BorderLayout.CENTER);
        add(inputTextField, BorderLayout.SOUTH);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private void receiveMessages() {
        try {
            connectionToServer = new Socket(address, port);
            fromServerReader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));
            toServerWriter = new PrintWriter(new OutputStreamWriter(connectionToServer.getOutputStream()));

            initGui();

            while (true) {
                String message = fromServerReader.readLine();
                outputTextArea.append(message + "\n");
                outputScrollPane.getVerticalScrollBar().setValue(outputScrollPane.getVerticalScrollBar().getMaximum());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Verbindung zum Server \"" + address + "\" fehlgeschlagen.");
            dispose();
        } finally {
            if (connectionToServer != null) {
                try {
                    connectionToServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fromServerReader != null) {
                try {
                    fromServerReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (toServerWriter != null) {
                toServerWriter.close();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String message = inputTextField.getText();
            if (!message.isEmpty()) {
                toServerWriter.println(message);
                toServerWriter.flush();
                inputTextField.setText("");
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        new ChatClient(3141);
    }
}
