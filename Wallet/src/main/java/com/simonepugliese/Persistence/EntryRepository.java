package com.simonepugliese.Persistence;

import com.simonepugliese.Core.IEntryRepository;
import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Model.Field;
import com.simonepugliese.Model.FieldType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Concrete implementation of the {@link IEntryRepository} interface.
 * <p>
 * This class is public so it can be instantiated by the application's
 * factory or main class.
 */
public final class EntryRepository implements IEntryRepository {

    // --- Private SQL Constants ---
    private static final String TABLE_ENTRIES = "Entries";
    private static final String TABLE_FIELDS = "Fields";

    // UPSERT (Insert or Update) logic for an Entry
    private static final String UPSERT_ENTRY_SQL =
            "INSERT INTO " + TABLE_ENTRIES + " (id, description, category) VALUES (?, ?, ?) " +
                    "ON CONFLICT(id) DO UPDATE SET description = ?, category = ?";

    private static final String DELETE_FIELDS_SQL = "DELETE FROM " + TABLE_FIELDS + " WHERE entry_id = ?";

    private static final String INSERT_FIELD_SQL =
            "INSERT INTO " + TABLE_FIELDS + " (id, entry_id, name, value, type, sensitive) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ENTRY_BY_ID_SQL = "SELECT * FROM " + TABLE_ENTRIES + " WHERE id = ?";
    private static final String SELECT_FIELDS_BY_ENTRY_ID_SQL = "SELECT * FROM " + TABLE_FIELDS + " WHERE entry_id = ?";
    private static final String SELECT_ALL_ENTRY_SUMMARIES_SQL = "SELECT id, description, category FROM " + TABLE_ENTRIES;
    private static final String DELETE_ENTRY_BY_ID_SQL = "DELETE FROM " + TABLE_ENTRIES + " WHERE id = ?";

    private final DbConnector dbConnector;

    /**
     * Constructs a new EntryRepository.
     * It immediately gets the DbConnector instance.
     */
    public EntryRepository() {
        this.dbConnector = DbConnector.getInstance();
        // The DbConnector's constructor already called initializeDatabase()
    }

    /**
     * Saves or updates an Entry and all its fields in a single transaction.
     * This method implements the Template Method pattern (UPSERT).
     */
    @Override
    public void save(Entry entry) {
        Connection conn = null;
        try {
            conn = dbConnector.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Step 1: Save the Entry (UPSERT)
            try (PreparedStatement ps = conn.prepareStatement(UPSERT_ENTRY_SQL)) {
                ps.setString(1, entry.getId());
                ps.setString(2, entry.getDescription());
                ps.setString(3, entry.getCategory().name());
                // Parameters for the ON CONFLICT UPDATE
                ps.setString(4, entry.getDescription());
                ps.setString(5, entry.getCategory().name());
                ps.executeUpdate();
            }

            // Step 2: Delete all old Fields for this Entry (to ensure clean state)
            try (PreparedStatement ps = conn.prepareStatement(DELETE_FIELDS_SQL)) {
                ps.setString(1, entry.getId());
                ps.executeUpdate();
            }

            // Step 3: Insert all current Fields (Batch insert)
            try (PreparedStatement ps = conn.prepareStatement(INSERT_FIELD_SQL)) {
                for (Map.Entry<String, Field> fieldEntry : entry.getFields().entrySet()) {
                    Field field = fieldEntry.getValue();
                    ps.setString(1, UUID.randomUUID().toString()); // New unique ID for the field
                    ps.setString(2, entry.getId());                 // Foreign Key to the Entry
                    ps.setString(3, fieldEntry.getKey());           // Field Name (from map key)
                    ps.setString(4, field.getValue());              // Field Value (encrypted)
                    ps.setString(5, field.getType().name());        // Field Type
                    ps.setBoolean(6, field.isSensitive());          // Sensitivity
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            System.err.println("Transaction failed. Rolling back.");
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace(); //TODO: Log rollback failure
            }
            throw new RuntimeException("Failed to save entry", e);
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace(); //TODO: Log error
            }
        }
    }

    @Override
    public Optional<Entry> findById(String id) {
        try (Connection conn = dbConnector.getConnection()) {
            // 1. Find the main Entry row
            Entry entry = null;
            try (PreparedStatement ps = conn.prepareStatement(SELECT_ENTRY_BY_ID_SQL)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        entry = mapResultSetToEntry(rs);
                    }
                }
            }

            // 2. If Entry exists, load its associated Fields
            if (entry != null) {
                try (PreparedStatement ps = conn.prepareStatement(SELECT_FIELDS_BY_ENTRY_ID_SQL)) {
                    ps.setString(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            mapResultSetToField(rs, entry);
                        }
                    }
                }
                return Optional.of(entry);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find entry by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Entry> findAllSummaries() {
        List<Entry> summaries = new ArrayList<>();
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_ENTRY_SUMMARIES_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                summaries.add(mapResultSetToEntry(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load entry summaries", e);
        }
        return summaries;
    }

    @Override
    public boolean deleteById(String id) {
        try (Connection conn = dbConnector.getConnection()) {
            // We rely on "ON DELETE CASCADE" in the table definition.
            // If that was set, we only need to delete from the Entries table.
            try (PreparedStatement ps = conn.prepareStatement(DELETE_ENTRY_BY_ID_SQL)) {
                ps.setString(1, id);
                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete entry", e);
        }
    }

    // --- Private Helper Methods (Mapping) ---

    /**
     * Maps a ResultSet row to a new Entry object.
     * This helper assumes the ResultSet columns match the Entry definition.
     *
     * @param rs The ResultSet, positioned at a valid row.
     * @return A new {@link Entry} object, populated with metadata.
     * @throws SQLException if column labels are not found.
     */
    private Entry mapResultSetToEntry(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String description = rs.getString("description");
        Category category = Category.valueOf(rs.getString("category"));
        return new Entry(id, description, category);
    }

    /**
     * Maps a ResultSet row to a new Field object and adds it to the
     * provided Entry.
     *
     * @param rs    The ResultSet, positioned at a valid row from the Fields table.
     * @param entry The Entry object to which the new field will be added.
     * @throws SQLException if column labels are not found.
     */
    private void mapResultSetToField(ResultSet rs, Entry entry) throws SQLException {
        String name = rs.getString("name");
        String value = rs.getString("value");
        FieldType type = FieldType.valueOf(rs.getString("type"));
        boolean sensitive = rs.getBoolean("sensitive");

        entry.putField(name, new Field(value, type, sensitive));
    }
}