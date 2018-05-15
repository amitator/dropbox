package ru.geekbrains;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class Server {
    private static final int SERVER_PORT = 8189;
    private ServerSocket serverSocket;
    private Vector<ClientHandler> clients;

    public Server() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server started at port: " + SERVER_PORT);
            Socket socket = serverSocket.accept();
            System.out.println("Client connected");
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            while (true) {
                String str = in.nextLine();
                System.out.println("Client: " + str);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}
