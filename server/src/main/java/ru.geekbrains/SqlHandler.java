package ru.geekbrains;

import java.sql.*;

public class SqlHandler {
    private static Connection connection;   //we need only 1 connection so can make it static
    private static Statement statement;

    public static void connect() throws ClassNotFoundException, SQLException { //throw them out otherwise noone would know if something goes wrong
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:server/data.db");
        statement = connection.createStatement();
        prepare();
    }

    private static void prepare() {
        try {
            statement.executeUpdate("CREATE TABLE IF NOT EXIST users (id       INTEGER PRIMARY KEY AUTOINCREMENT, login    TEXT    UNIQUE, password TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isAuthorized(String login, String pass){
        try {
            ResultSet rs = statement.executeQuery(String.format(
                    "SELECT login FROM users WHERE login = '%s' AND password = '%s';", login, pass));
            if (rs.next()) {
                return true;
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
