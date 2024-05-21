package ru.netology.threads;

import ru.netology.Server;

import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;

public class ClientProcessorThread implements Runnable {
    private final Server SERVER;
    private final ServerSocket SERVER_SOCKET;

    public ClientProcessorThread(Server server, ServerSocket serverSocket) {
        this.SERVER = server;
        this.SERVER_SOCKET = serverSocket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            while (!SERVER_SOCKET.isClosed()) {
                String message = reader.readLine();
                String SERVER_NAME = "SERVER";
                SERVER.onReceiveMessage(SERVER_NAME + ": " + message);
            }
        } catch (IOException e) {
            SERVER.onException("Client Process Exception", e);
        }
    }
}