package ru.geekbrains;

import main.java.ru.geekbrains.AbstractMessage;
import main.java.ru.geekbrains.AuthMessage;
import main.java.ru.geekbrains.CommandMessage;

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

            new Thread(() -> {
                isAuthorized = false;
                try {
                    while (true){
                        AbstractMessage abstractMessage = (AbstractMessage) in.readObject();
                        if (abstractMessage instanceof AuthMessage){
                            AuthMessage am = (AuthMessage) abstractMessage;
                            isAuthorized = SqlHandler.isAuthorized(am.getLogin(), am.getPassword());
                            if (isAuthorized){
                                sendMessage(new CommandMessage(CommandMessage.AUTH_OK, am.getLogin()));
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
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

    public void sendMessage(AbstractMessage abstractMessage){
        try {
            out.writeObject(abstractMessage);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
