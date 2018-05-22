package main.java.ru.geekbrains;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileDataMessage extends AbstractMessage {
    private String fileName;
    private long size;
    private byte[] data;

    public FileDataMessage(String fileName) {
        try {
            this.fileName = fileName;
            this.size = Files.size(Paths.get(fileName));
            this.data = Files.readAllBytes(Paths.get(fileName)); //cuz of NIO we can just Files.readAllBytes(Paths.get(fileName))
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}