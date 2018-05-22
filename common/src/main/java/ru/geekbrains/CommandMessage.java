package main.java.ru.geekbrains;

public class CommandMessage extends AbstractMessage{
    private int cmd;
    private Object[] attachment;

    public static final int AUTH_OK = 3339482;

    public CommandMessage(int cmd, Object... attachment) {
        this.cmd = cmd;
        this.attachment = attachment;
    }
}
