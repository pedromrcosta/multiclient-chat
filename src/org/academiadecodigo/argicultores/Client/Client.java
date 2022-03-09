package org.academiadecodigo.argicultores.Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client{

    private String hostName = "localhost";
    private final int PORT_NUMBER = 8080;
    private Scanner scanner = new Scanner(System.in);

    private Socket clientSocket = null;
    private BufferedWriter writer;
    private BufferedReader reader;

    public static void main(String[] args) {
        Client client = new Client();

        try {
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() throws IOException {

        clientSocket = new Socket(hostName, PORT_NUMBER);
        System.out.print("CONNECTED TO: ");
        System.out.println(hostName + ":" + PORT_NUMBER);

        writer = new BufferedWriter(new PrintWriter(clientSocket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        Thread sender = new Thread(new ClientSend());
        Thread receive = new Thread(new ClientReceive());

        sender.start();
        receive.start();
    }

    private class ClientReceive implements Runnable {

        String message = "";

        @Override
        public void run() {
            try {

                while (!clientSocket.isClosed()) {
                    message = reader.readLine();
                    if (message == null) {break;}
                    System.out.println(message);
                }

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientSend implements Runnable {

        @Override
        public void run() {
            String lineWrite = "";
            try {

            while (!clientSocket.isClosed()) {

                    System.out.print("> ");
                    lineWrite = scanner.nextLine();
                    if (lineWrite.equals("!quit") || lineWrite == null) {
                        break;
                    }
                    writer.write(lineWrite);
                    writer.newLine();
                    writer.flush();
            }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                System.out.println((clientSocket.isClosed() ? "Server down" : "Client exits"));
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
