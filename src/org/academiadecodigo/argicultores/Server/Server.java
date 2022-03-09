package org.academiadecodigo.argicultores.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int PORT_NUMBER = 8080;;
    private ServerSocket serverSocket;
    private BufferedWriter writer;
    private BufferedReader reader;

    private LinkedList<Socket> clientList = new LinkedList<>();
    private Iterator it = clientList.iterator();
    private int counter;


    public static void main(String[] args) {

        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void start() throws IOException {

        serverSocket = new ServerSocket(PORT_NUMBER);
        System.out.println("WAITING FOR CONNECTION...");

        Thread serverListen = new Thread(new ServerListen(this));
        serverListen.start();
    }

    public synchronized void sendMessage(String message, Socket sender, String name) throws IOException {

        System.out.println(name + ": " + message);

        for (Socket s : clientList) {
            writer = new BufferedWriter(new PrintWriter(s.getOutputStream()));
            if (!s.equals(sender)) {
                if (message.length() > 7 && message.substring(0,8).equals("/whisper")) {
                    if (Integer.parseInt(message.split(" ")[1].substring(4,5)) == Integer.parseInt(name.substring(name.length() - (int)Math.ceil(counter/10)))) {
                        sendMessage(message.substring(message.indexOf(" ")).substring(message.indexOf(" ")-1));
                        break;
                    }
                } else {
                    sendMessage(message);
                }
            }
        }
    }

    private void sendMessage(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    private class ServerListen implements Runnable {

        private Server server;

        public ServerListen(Server server) {
            this.server = server;
        }

        @Override
        public void run() {

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("CONNECTION ACCEPTED FROM " + clientSocket.getInetAddress().getHostAddress());

                    clientList.add(clientSocket);

                    Thread client = new Thread(new ClientHandler(server, clientSocket));
                    client.setName("user" + counter);
                    counter++;
                    client.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
