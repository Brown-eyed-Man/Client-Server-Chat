package ru.netology.threads;

import ru.netology.Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionReceiver {
    private final Server SERVER;
    private final Socket SOCKET;
    private final Thread receiveConnectionThread;
    private String CLIENT_NAME;
    BufferedReader reader;
    BufferedWriter writer;

    public ConnectionReceiver(Server server, Socket socket) {
        this.SERVER = server;
        this.SOCKET = socket;

        receiveConnectionThread = new Thread(() -> {
            try {
                reader = new BufferedReader(new InputStreamReader(SOCKET.getInputStream(), StandardCharsets.UTF_8));
                writer = new BufferedWriter(new OutputStreamWriter(SOCKET.getOutputStream(), StandardCharsets.UTF_8));

                SERVER.onConnectionReady(ConnectionReceiver.this);
                CLIENT_NAME = reader.readLine();
                String SERVER_NAME = "SERVER";
                SERVER.onReceiveMessage(SERVER_NAME + ": Client " + SOCKET.getInetAddress() + ": " + SOCKET.getPort() + " has chosen the nickname - " + CLIENT_NAME + ".");
                SERVER.onReceiveMessage(SERVER_NAME + ": " + CLIENT_NAME + ", welcome to our chat!");

                while (true) {
                    try {
                    String response = reader.readLine();
                    if (response.equalsIgnoreCase(CLIENT_NAME + ": /exit")) {
                        disconnect();
                    }
                    SERVER.onReceiveMessage(response);
                    } catch (NullPointerException e) {
                        SERVER.onDisconnect(this);
                        break;
                    }
                }
            } catch (IOException e) {
                disconnect();
                SERVER.onException(e);
            }
        });
        receiveConnectionThread.start();
    }

    public synchronized void sendMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            SERVER.onException(e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        receiveConnectionThread.interrupt();
        SERVER.onDisconnect(this);
    }

    public String getCLIENT_NAME() {
        return CLIENT_NAME;
    }

    public Socket getSOCKET() {
        return SOCKET;
    }

    @Override
    public String toString() {
        return SOCKET.getInetAddress() + ": " + SOCKET.getPort();
    }
}