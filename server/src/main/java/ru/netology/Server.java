package ru.netology;

import ru.netology.threads.*;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static ServerSocket SERVER_SOCKET;
    private static final int PORT = 27015;
    private static final List<ConnectionReceiverThread> CLIENT_CONNECTIONS = new CopyOnWriteArrayList<>();
    private Thread clientProcessorThread;
    private Thread connectionReceiverThread;

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        try {
            SERVER_SOCKET = new ServerSocket(PORT);
            sendMessageToAllClients("Server has been launched.");

            clientProcessorThread = new Thread(new ClientProcessorThread(this));
            clientProcessorThread.start();

            while (true) {
                try {
                    Socket socket = SERVER_SOCKET.accept();
                    connectionReceiverThread = new Thread(new ConnectionReceiverThread(this, socket));
                    connectionReceiverThread.start();
                } catch (IOException e) {
                    return;
                }
            }
        } catch (IOException e) {
            onException(e);
        }
    }

    public synchronized void onConnectionReady(ConnectionReceiverThread connection) {
        CLIENT_CONNECTIONS.add(connection);
        sendMessageToAllClients("New client connected: " + connection);
    }

    public synchronized void onReceiveMessage(String value) {
        sendMessageToAllClients(value);
    }

    public synchronized void onDisconnect(ConnectionReceiverThread connection) {
        CLIENT_CONNECTIONS.remove(connection);
        sendMessageToAllClients(String.format("Client %s:%d (%s) disconnected.",
                connection.getSOCKET().getInetAddress(),
                connection.getSOCKET().getPort(),
                connection.getCLIENT_NAME()));
    }

    public synchronized void onException(Exception e) {
        System.out.println("Exception: " + e);
        ChatLogger.getInstance().log("Exception: " + e);
    }

    public synchronized void closeServer() {
        try {
            for (ConnectionReceiverThread client : CLIENT_CONNECTIONS) {
                CLIENT_CONNECTIONS.remove(client);
                client.disconnect();
            }
            clientProcessorThread.interrupt();
            connectionReceiverThread.interrupt();
            SERVER_SOCKET.close();
            sendMessageToAllClients("Server has been stopped.\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToAllClients(String message) {
        System.out.println(message);
        ChatLogger.getInstance().log(message);
        for (ConnectionReceiverThread client : CLIENT_CONNECTIONS) {
            client.sendMessage(message);
        }
    }
}