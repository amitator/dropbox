package ru.geekbrains;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isAuthorized;

    public ClientHandler (final Server server, final Socket socket){
        try {
            this.server = server;
            this.socket = socket;
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
            isAuthorized = false;

            new Thread(() -> {
                try {
                    while (true){
                        String msg = in.readUTF();
                        if (msg.startsWith("/auth")){
                            String[] tokens = msg.split(" ");
                            isAuthorized = SqlHandler.isAuthorized(tokens[1], tokens[2]);
                            if (isAuthorized){
                                out.writeUTF("/authok");
                            } else {
                                out.writeUTF("Wrong Login/Password pair.");
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
