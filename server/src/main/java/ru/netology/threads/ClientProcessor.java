package ru.netology.threads;

import ru.netology.Server;

import java.util.Scanner;

public class ClientProcessor {
    private final Server SERVER;
    private final Thread receiveThread;

    public ClientProcessor(Server server) {
        this.SERVER = server;
        receiveThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    String message = scanner.nextLine();
                    String SERVER_NAME = "SERVER";
                    if (message.equalsIgnoreCase("/stop server")) {
                        SERVER.closeServer();
                        closeServer();
                        break;
                    }
                    SERVER.onReceiveMessage(SERVER_NAME + ": " + message);
                }
            }
        });
        receiveThread.start();
    }

    private void closeServer() {
        receiveThread.interrupt();
    }
}