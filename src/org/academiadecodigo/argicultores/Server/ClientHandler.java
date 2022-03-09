package org.academiadecodigo.argicultores.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Server server;
    private Socket socket;
    private String name;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String messageReceive = "";

            name = Thread.currentThread().getName();

            while (true) {
                messageReceive = reader.readLine();
                if (messageReceive == null) {break;}
                server.sendMessage(messageReceive, this.socket, name);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
