package ru.geekbrains;


import main.java.ru.geekbrains.*;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String login;
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
                                this.login = am.getLogin();
                                sendMessage(new CommandMessage(CommandMessage.AUTH_OK, login));
                                sendMessage(getFileStructureMessage());
                                break;
                            } else {
                                sendMessage(new CommandMessage(CommandMessage.AUTH_WRONG, null));
                            }
                        }
                    }
                    while (true){
                        AbstractMessage abstractMessage = (AbstractMessage) in.readObject();
                        if (abstractMessage instanceof FileDataMessage) {
                            FileDataMessage fdm = (FileDataMessage) abstractMessage;
                            Files.write(Paths.get("server/storage/" + login + "/" + fdm.getFileName()), fdm.getData(), StandardOpenOption.CREATE);
                            sendMessage(getFileStructureMessage());
                        }
                        if (abstractMessage instanceof CommandMessage) {
                            CommandMessage cmd = (CommandMessage) abstractMessage;
                            if (cmd.getCmd() == CommandMessage.DOWNLOAD_FILE){
                                FileDataMessage fdm = new FileDataMessage("server/storage/" + login + "/" + cmd.getAttachment()[0]);
                                sendMessage(fdm);
                            }
                            if (cmd.getCmd() == CommandMessage.FILE_LIST_REQUEST){
                                sendMessage(getFileStructureMessage());
                            }
                            if (cmd.getCmd() == CommandMessage.DELETE_FILE) {
                                Files.delete(Paths.get("server/storage/" + login + "/" + cmd.getAttachment()[0]));
                                sendMessage(getFileStructureMessage());
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
                   System.out.println("Client disconnected.");
                }
            }).start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public FileListMessage getFileStructureMessage(){
        return new FileListMessage(getFilesList());
    }

    public List<String> getFilesList(){
        List<String> files = new ArrayList<>();
        try {
            Files.newDirectoryStream(Paths.get("server/storage/" + login)).forEach(path -> files.add(path.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    public void sendMessage(AbstractMessage abstractMessage){
        try {
            out.writeObject(abstractMessage);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
