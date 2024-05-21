package ru.netology;

import ru.netology.threads.ClientMessageReceiverThread;
import ru.netology.threads.ClientMessageSenderThread;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_SETTINGS = "client/src/main/java/ru/netology/settings.txt";
    private static String IP;
    private static int port;
    private final Thread senderThread;
    private final Thread receiverThread;

    public static void main(String[] args) {
        new Client();
    }

    private Client() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SERVER_SETTINGS))) {
            String s;
            while ((s = reader.readLine()) != null) {
                if (s.contains("IP:")) {
                    String[] ip = s.split(" ");
                    IP = ip[1];
                }
                if (s.contains("port:")) {
                    String[] port = s.split(" ");
                    Client.port = Integer.parseInt(port[1]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("File reading exception: " + e);
        }

        try {
            Socket clientSocket = new Socket(IP, port);
            receiverThread = new Thread(new ClientMessageReceiverThread(this, clientSocket));
            receiverThread.start();

            senderThread = new Thread(new ClientMessageSenderThread(this, clientSocket));
            senderThread.start();

            System.out.println("You connected to Chat.");
            System.out.println("To send any message, please, write it and press \"Enter\".");
            System.out.println("If you want to leave this Chat - just write \"/exit\".\n");

            System.out.println("Please, Introduce yourself:");

        } catch (IOException e) {
            throw new RuntimeException("Server hasn't launched or you made mistake in IP or port. =(\n" + e);
        }
    }

    public synchronized void stopClient(Socket clientSocket) {
        receiverThread.interrupt();
        senderThread.interrupt();

        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Close Socket Exception: \n" + e);
        }
    }
}