package ru.netology.threads;

import ru.netology.Client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientMessageSenderThread implements Runnable {
    private final Client CLIENT;
    private final Socket CLIENT_SOCKET;

    public ClientMessageSenderThread(Client client, Socket clientSocket) {
        this.CLIENT = client;
        this.CLIENT_SOCKET = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(CLIENT_SOCKET.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String name = reader.readLine();
            sendMessage(writer, name);

            while (true) {
                String message = reader.readLine();
                if (message.equalsIgnoreCase("/exit")) {
                    CLIENT.stopClient(CLIENT_SOCKET);
                    break;
                }
                sendMessage(writer, name + ": " + message);
            }
        } catch (IOException e) {
            CLIENT.stopClient(CLIENT_SOCKET);
        }
    }

    public synchronized void sendMessage(BufferedWriter writer, String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            CLIENT.stopClient(CLIENT_SOCKET);
        }
    }
}