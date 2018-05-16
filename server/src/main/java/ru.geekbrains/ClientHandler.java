package ru.geekbrains;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isAuthorized;

    public ClientHandler (final Server server, final Socket socket){
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            isAuthorized = false;

            new Thread(() -> {
                try {
                    while (true){
                        String msg = in.readUTF();
                        System.out.println(msg);
                        if (msg.startsWith("/auth")){
                            String[] tokens = msg.split(" ");
                            for (String a: tokens) {
                                System.out.println(a);
                            }
                            isAuthorized = SqlHandler.isAuthorized(tokens[1], tokens[2]);
                            if (isAuthorized){
                                out.writeUTF("/authok");
                                System.out.println("/authok");
                            } else {
                                out.writeUTF("Wrong Login/Password pair.");
                                System.out.println("Wrong Login/Password pair.");
                            }
                        }
                    }
                } catch (IOException ex){
                    ex.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        in.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Client disconnected.");
                }
            }).start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
