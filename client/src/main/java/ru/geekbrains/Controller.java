package ru.geekbrains;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import main.java.ru.geekbrains.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {
    Socket socket = null;
    ObjectInputStream in;
    ObjectOutputStream out;
    String login;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passField;

    @FXML
    HBox authPanel;

    @FXML
    HBox cmdPanel;

    @FXML
    ListView<String> mainList;

    public void setLogin(String login){
        if (login == null){
            authPanel.setVisible(true);
            authPanel.setManaged(true);
            cmdPanel.setVisible(false);
            cmdPanel.setManaged(false);
        } else {
            authPanel.setVisible(false);
            authPanel.setManaged(false);
            cmdPanel.setVisible(true);
            cmdPanel.setManaged(true);
        }
    }


    public void sendAuthMessage(){
        if (socket == null || socket.isClosed())
            connect();
        AuthMessage authMessage = new AuthMessage(loginField.getText(), passField.getText());
        loginField.clear();
        passField.clear();
        try {
            out.writeObject(authMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            System.out.println("Client started");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Thread t = new Thread(() -> {
                try {
                    while (true){
                        AbstractMessage abstractMessage = (AbstractMessage) in.readObject();
                        if (abstractMessage instanceof CommandMessage) {
                            CommandMessage cm = (CommandMessage)abstractMessage;
                            if (cm.getCmd() == CommandMessage.AUTH_OK){
                                login = (String) cm.getAttachment()[0];
                                System.out.println("AUTH OK with Login: " + login);
                                setLogin(login);
                                break;
                            }
                        }
                    }
                    while (true) {
                        AbstractMessage abstractMessage = (AbstractMessage) in.readObject();
                        if (abstractMessage instanceof FileListMessage) {
                            FileListMessage flm = (FileListMessage) abstractMessage;
                            Platform.runLater(() -> {
                                mainList.getItems().clear();
                                for (int i = 0; i < flm.getFiles().size(); i++) {
                                    mainList.getItems().add(flm.getFiles().get(i));
                                }
                            });

                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}