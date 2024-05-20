package ru.netology.threads;

import ru.netology.Client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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
             Scanner scanner = new Scanner(System.in)) {
            String name = scanner.nextLine();
            sendMessage(writer, name);

            while (true) {
                String message = scanner.nextLine();
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
            throw new RuntimeException(e);
        }
    }
}