package ru.geekbrains;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
}
