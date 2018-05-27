package ru.geekbrains;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.java.ru.geekbrains.*;

import java.io.*;
import java.net.Socket;


public class ClientMain {
    public static void main(String[] args) {
        Socket socket = null;
        ObjectInputStream in;
        ObjectOutputStream out;
        String login;

        try {
            socket = new Socket("localhost", 8189);
            System.out.println("Client started");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            AuthMessage authMessage =  new AuthMessage("user1", "password1");
            out.writeObject(authMessage);
            while (true){
                AbstractMessage abstractMessage = (AbstractMessage) in.readObject();
                if (abstractMessage instanceof CommandMessage) {
                    CommandMessage cm = (CommandMessage)abstractMessage;
                    if (cm.getCmd() == CommandMessage.AUTH_OK){
                        login = (String) cm.getAttachment()[0];
                        System.out.println("AUTH OK with Login: " + login);
                        break;
                    }
                }
            }
            FileDataMessage fdm = new FileDataMessage("client/123.txt");
            out.writeObject(fdm);
            while (true) {
                AbstractMessage abstractMessage = (AbstractMessage) in.readObject();
                if (abstractMessage instanceof FileListMessage) {
                    FileListMessage flm = (FileListMessage) abstractMessage;
                    for (int i = 0; i < flm.getFiles().size(); i++) {
                        System.out.println(flm.getFiles().get(i));
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
