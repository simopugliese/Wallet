package com.simonepugliese.Persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton database connector for SQLite.
 * Manages a single connection URL and initializes the database schema.
 */
public class DbConnector {

    private static volatile DbConnector instance;
    private static String JDBC_URL = "jdbc:sqlite:wallet.db";

    // --- Table Definitions ---
    private static final String TABLE_ENTRIES = "Entries";
    private static final String TABLE_FIELDS = "Fields";

    /**
     * Private constructor for Singleton pattern.
     */
    private DbConnector() {
        initializeDatabase();
    }

    /**
     * Gets the single instance of the DbConnector.
     *
     * @return The singleton DbConnector instance.
     */
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

    /**
     * Sets a custom path for the database file.
     * Must be called *before* {@link #getInstance()} for the first time.
     *
     * @param absolutePath The full, absolute path to the .db file.
     */
    public static void setJdbcUrl(String absolutePath) {
        if (instance != null) {
            throw new IllegalStateException("Cannot set URL after instance is created.");
        }
        JDBC_URL = "jdbc:sqlite:" + absolutePath;
    }

    /**
     * Gets a new connection to the database.
     *
     * @return A {@link Connection} to the SQLite database.
     * @throws SQLException if a database access error occurs.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    /**
     * Initializes the database schema.
     * Creates the new Entries and Fields tables if they don't exist.
     * This method is called automatically by the constructor.
     */
    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // SQL to create the Entries table
            String createEntriesSql = "CREATE TABLE IF NOT EXISTS " + TABLE_ENTRIES + " ("
                    + "id TEXT PRIMARY KEY, "
                    + "description TEXT NOT NULL, "
                    + "category TEXT NOT NULL)";
            stmt.execute(createEntriesSql);

            // SQL to create the Fields table
            String createFieldsSql = "CREATE TABLE IF NOT EXISTS " + TABLE_FIELDS + " ("
                    + "id TEXT PRIMARY KEY, "
                    + "entry_id TEXT NOT NULL, "
                    + "name TEXT NOT NULL, "
                    + "value TEXT, "
                    + "type TEXT NOT NULL, "
                    + "sensitive INTEGER NOT NULL, "
                    + "FOREIGN KEY (entry_id) REFERENCES " + TABLE_ENTRIES + "(id) ON DELETE CASCADE)";
            stmt.execute(createFieldsSql);

            // Create an index for fast field lookups by entry_id
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_entry_id ON " + TABLE_FIELDS + " (entry_id)");

            System.out.println("Database tables checked/created successfully.");

        } catch (SQLException e) {
            e.printStackTrace(); //TODO: more robust logging
            System.err.println("Error initializing database: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}