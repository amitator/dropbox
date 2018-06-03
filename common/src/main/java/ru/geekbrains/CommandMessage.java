package main.java.ru.geekbrains;

public class CommandMessage extends AbstractMessage{
    private int cmd;
    private Object[] attachment;

    public static final int AUTH_WRONG = 3241235;
    public static final int AUTH_OK = 3339482;
    public static final int DOWNLOAD_FILE = 98765423;
    public static final int FILE_LIST_REQUEST = 1234254;
    public static final int DELETE_FILE = 12455234;

    public int getCmd() {
        return cmd;
    }

    public Object[] getAttachment() {
        return attachment;
    }

    public CommandMessage(int cmd, Object... attachment) {
        this.cmd = cmd;
        this.attachment = attachment;
    }
}
