package org.example.persistence.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionConfig {

    private static final String URL = "jdbc:mysql://localhost/board";
    private static final String USER = "root";
    private static final String PASSWORD = "0711Abcde!";


    public static Connection getConnection() throws SQLException {
        var connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connection.setAutoCommit(false);
        return connection;
    }
}
