package ru.netology.threads;

import ru.netology.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientMessageReceiverThread implements Runnable{
    private final Client CLIENT;
    private final Socket CLIENT_SOCKET;

    public ClientMessageReceiverThread(Client client, Socket clientSocket) {
        this.CLIENT = client;
        this.CLIENT_SOCKET = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(CLIENT_SOCKET.getInputStream(), StandardCharsets.UTF_8))) {
            while (true) {
                String response = reader.readLine();
                System.out.println(response);
                if (response.equalsIgnoreCase("SERVER: /stop server")) {
                    CLIENT.stopClient(CLIENT_SOCKET);
                    break;
                }
            }
        } catch (IOException e) {
            CLIENT.stopClient(CLIENT_SOCKET);
        }
    }
}
