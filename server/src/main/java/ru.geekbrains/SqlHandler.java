package ru.geekbrains;

import java.sql.*;

public class SqlHandler {
    private static Connection connection;
    private static Statement statement;

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:server/data.db");
        statement = connection.createStatement();
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
                    "SELECT login FROM users " +
                            "WHERE login = '%s' AND password = '%s;", login, pass));
            if (rs.next()) {
                return true;
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
