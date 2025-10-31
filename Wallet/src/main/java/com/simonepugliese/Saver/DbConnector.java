package com.simonepugliese.Saver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnector {
    private static DbConnector instance;
    private static String JDBC_URL = "jdbc:sqlite:wallet.db";

    public static void setJdbcUrl(String absolutePath) {
        JDBC_URL = "jdbc:sqlite:" + absolutePath;
        if (instance != null) {
            instance.initializeDatabase();
        }
    }

    private DbConnector() {
        initializeDatabase();
    }

    public static DbConnector getInstance() {
        if (instance == null) {
            synchronized (DbConnector.class) {
                if (instance == null) {
                    instance = new DbConnector();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createLoginSql = "CREATE TABLE IF NOT EXISTS LoginsBasic ("
                    + "description TEXT PRIMARY KEY, "
                    + "username TEXT NOT NULL, "
                    + "password TEXT, "
                    + "urlSito TEXT)";
            stmt.execute(createLoginSql);

            String createCreditCardSql = "CREATE TABLE IF NOT EXISTS CreditCards ("
                    + "description TEXT PRIMARY KEY, "
                    + "owner TEXT, "
                    + "bank TEXT, "
                    + "number TEXT, "
                    + "cvv TEXT, "
                    + "expiration TEXT)";
            stmt.execute(createCreditCardSql);

            System.out.println("Database tables checked/created successfully.");

        } catch (SQLException e) {
            System.err.println("Errore durante l'inizializzazione del database: " + e.getMessage());
            throw new RuntimeException("Impossibile inizializzare il database.", e);
        }
    }
}