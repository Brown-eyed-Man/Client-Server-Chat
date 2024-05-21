package ru.netology.threads;

import ru.netology.Server;

import java.util.Scanner;

public class ClientProcessorThread implements Runnable {
    private final Server SERVER;

    public ClientProcessorThread(Server server) {
        this.SERVER = server;

    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String message = scanner.nextLine();
                String SERVER_NAME = "SERVER";
                if (message.equalsIgnoreCase("/stop server")) {
                    SERVER.onReceiveMessage(SERVER_NAME + ": " + message);
                    SERVER.closeServer();
                    Thread.currentThread().interrupt();
                    break;
                }
                SERVER.onReceiveMessage(SERVER_NAME + ": " + message);
            }
        }
    }
}