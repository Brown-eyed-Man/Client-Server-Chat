package ru.netology;

import ru.netology.threads.ClientProcessor;
import ru.netology.threads.ConnectionReceiver;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static ServerSocket SERVER_SOCKET;
    private static final int PORT = 27015;
    private static final List<ConnectionReceiver> CLIENT_CONNECTIONS = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
            try {
                SERVER_SOCKET = new ServerSocket(PORT);
                sendMessageToAllClients("Server has been launched.");

                new ClientProcessor(this);

                while (true) {
                    try {
                        Socket socket = SERVER_SOCKET.accept();
                        new ConnectionReceiver(this, socket);
                    } catch (IOException e) {
                        return;
                    }
                }
            } catch (IOException e) {
                onException(e);
            }
    }

    public synchronized void onConnectionReady(ConnectionReceiver connection) {
        CLIENT_CONNECTIONS.add(connection);
        sendMessageToAllClients("New client connected: " + connection);
    }

    public synchronized void onReceiveMessage(String value) {
        sendMessageToAllClients(value);
    }

    public synchronized void onDisconnect(ConnectionReceiver connection) {
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
            for (ConnectionReceiver client : CLIENT_CONNECTIONS) {
                CLIENT_CONNECTIONS.remove(client);
                client.disconnect();
            }
            SERVER_SOCKET.close();
            sendMessageToAllClients("Server has been stopped.\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToAllClients(String message) {
        System.out.println(message);
        ChatLogger.getInstance().log(message);
        for (ConnectionReceiver client : CLIENT_CONNECTIONS) {
            client.sendMessage(message);
        }
    }
}