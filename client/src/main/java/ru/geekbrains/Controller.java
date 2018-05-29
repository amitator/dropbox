package ru.geekbrains;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.ru.geekbrains.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    Socket socket = null;
    ObjectInputStream in;
    ObjectOutputStream out;
    String login;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void sendAuthMessage(){
        if (socket == null || socket.isClosed())
            connect();
        AuthMessage authMessage = new AuthMessage(loginField.getText(), passField.getText());
    }

    public void connect() {
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
