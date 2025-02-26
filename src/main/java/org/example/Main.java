package org.example;

import org.example.persistence.migration.MigrationsStrategy;

import java.sql.SQLException;

import static org.example.persistence.config.ConnectionConfig.getConnection;

public class Main {
    public static void main(String[] args) throws SQLException {

        try (var connection = getConnection()) {
            new MigrationsStrategy(connection).executeMigration();
        }
    }
}