package ru.netology;

import ru.netology.threads.*;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final int PORT = 27015;
    private static final List<ConnectionReceiverThread> CLIENT_CONNECTIONS = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        try {
            ServerSocket SERVER_SOCKET = new ServerSocket(PORT);
            sendMessageToAllClients("Server has been launched.");

            Thread clientProcessorThread = new Thread(new ClientProcessorThread(this, SERVER_SOCKET));
            clientProcessorThread.start();

            while (!SERVER_SOCKET.isClosed()) {
                try {
                    Socket socket = SERVER_SOCKET.accept();
                    Thread connectionReceiverThread = new Thread(new ConnectionReceiverThread(this, socket));
                    connectionReceiverThread.start();
                } catch (IOException e) {
                    return;
                }
            }
        } catch (IOException e) {
            onException("Critical Error", e);
        }
    }

    public synchronized void onConnectionReady(ConnectionReceiverThread connection) {
        sendMessageToAllClients("New client connected: " + connection);
        CLIENT_CONNECTIONS.add(connection);
        connection.sendMessage("You connected to Chat.");
        connection.sendMessage("To send any message, please, write it and press \"Enter\".");
        connection.sendMessage("If you want to leave this Chat - just write \"/exit\".\n");
        connection.sendMessage("Please, Introduce yourself:");
    }

    public synchronized void onReceiveMessage(String value) {
        sendMessageToAllClients(value);
    }

    public synchronized void onDisconnect(ConnectionReceiverThread connection) {
        if (CLIENT_CONNECTIONS.remove(connection)) {
            sendMessageToAllClients(String.format("Client %s:%d \"%s\" disconnected.",
                    connection.getSOCKET().getInetAddress(),
                    connection.getSOCKET().getPort(),
                    connection.getCLIENT_NAME()));
        }
    }

    public synchronized void onException(String exceptionType, Exception e) {
        System.out.println(exceptionType + ": " + e);
        ChatLogger.getInstance().log(exceptionType + ": " + e);
    }

    public void sendMessageToAllClients(String message) {
        System.out.println(message);
        ChatLogger.getInstance().log(message);
        for (ConnectionReceiverThread client : CLIENT_CONNECTIONS) {
            client.sendMessage(message);
        }
    }
}