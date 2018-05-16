package ru.geekbrains;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private static final int SERVER_PORT = 8189;
    private ServerSocket serverSocket;
    private Vector<ClientHandler> clients;

    public Server() {
        try {
            SqlHandler.connect();
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server started at port: " + SERVER_PORT);
            clients = new Vector<ClientHandler>();

            while (true){
                Socket socket = serverSocket.accept();     //Waiting for client and not moving forward until client connected
                System.out.println("Client Connected");
                new ClientHandler(this, socket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            SqlHandler.disconnect();
        }
    }
}
