package main.java.ru.geekbrains;

public class AuthMessage extends AbstractMessage{
    private String login;
    private String password;

    public AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
