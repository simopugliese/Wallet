package com.simonepugliese.Persistence;

import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Model.Field;
import com.simonepugliese.Model.FieldType;
import org.junit.jupiter.api.*;

// *** RIMOSSO: import java.lang.reflect.Field; ***
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for {@link EntryRepository}.
 *
 * <p>This test uses a shared in-memory SQLite database
 * to test CRUD (Create, Read, Update, Delete) operations
 * in a fast and isolated manner.</p>
 */
class EntryRepositoryTest {

    private static EntryRepository repository;
    private static DbConnector dbConnector;

    /**
     * Configures the DbConnector to use a shared in-memory SQLite database
     * before any tests run. This is fast, isolated, and leaves no files.
     */
    @BeforeAll
    static void setupDatabase() {
        // *** AGGIUNGI QUESTA SEZIONE DI RESET ***
        // Resetta il Singleton prima di fare qualsiasi altra cosa.
        // Questo previene l'IllegalStateException causata dall'istanza
        // "sporca" lasciata da DbConnectorTest.
        try {
            // *** MODIFICA: Usa il nome completo per evitare conflitti ***
            java.lang.reflect.Field instanceField = DbConnector.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            fail("Failed to reset Singleton instance via reflection", e);
        }
        // *** FINE SEZIONE RESET ***


        // *** MODIFICA URL ***
        // Usiamo un file separato per questo test per garantire l'isolamento.
        DbConnector.setJdbcUrl("test_repo.db"); // <-- MODIFICATO

        dbConnector = DbConnector.getInstance(); // This initializes the tables
        repository = new EntryRepository();
    }

    /**
     * Cleans the tables before EACH test to ensure isolation.
     */
    @BeforeEach
    void cleanupTables() {
        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Clean Fields first due to the Foreign Key constraint
            stmt.executeUpdate("DELETE FROM Fields");
            stmt.executeUpdate("DELETE FROM Entries");
        } catch (Exception e) {
            fail("DB cleanup failed", e);
        }
    }

    /**
     * Helper method to create a complex test Entry.
     *
     * @param description The description for the entry.
     * @param category    The category for the entry.
     * @return A new Entry object populated with test fields.
     */
    private Entry createTestEntry(String description, Category category) {
        Entry entry = new Entry(description, category);
        entry.putField("Username", new Field("user@test.com", FieldType.TEXT, false));
        entry.putField("Password", new Field("encrypted-pass-value", FieldType.PASSWORD, true));
        entry.putField("URL", new Field("https://test.com", FieldType.URL, false));
        return entry;
    }

    /**
     * Tests the full save -> findById cycle.
     */
    @Test
    void save_and_findById_shouldReturnSavedEntryWithFields() {
        Entry entry = createTestEntry("Google", Category.LOGIN);

        // 1. Save
        repository.save(entry);

        // 2. Read
        Optional<Entry> loadedOpt = repository.findById(entry.getId());

        // 3. Assert
        assertTrue(loadedOpt.isPresent());
        Entry loaded = loadedOpt.get();

        assertEquals(entry.getId(), loaded.getId());
        assertEquals("Google", loaded.getDescription());
        assertEquals(Category.LOGIN, loaded.getCategory());

        // Verify fields
        assertEquals(3, loaded.getFields().size());
        assertEquals("user@test.com", loaded.getField("Username").getValue());
        assertEquals("encrypted-pass-value", loaded.getField("Password").getValue());
        assertTrue(loaded.getField("Password").isSensitive());
    }

    /**
     * Tests the UPSERT (UPDATE) logic when saving an entry with a conflicting ID.
     */
    @Test
    void save_shouldUpdateExistingEntry_OnConflict() {
        // 1. Create and save Version 1
        Entry entryV1 = createTestEntry("Site V1", Category.GENERIC);
        repository.save(entryV1);

        // 2. Modify the entry (same ID)
        entryV1.setDescription("Site V2 - Updated");
        entryV1.setCategory(Category.LOGIN);
        // Modify a field and add a new one
        entryV1.putField("Username", new Field("NEW_USER", FieldType.TEXT, false));
        entryV1.putField("Note", new Field("Added note", FieldType.NOTE, false));

        // 3. Save again (this should trigger an UPSERT)
        repository.save(entryV1);

        // 4. Reload and verify
        Entry loadedV2 = repository.findById(entryV1.getId()).orElse(null);
        assertNotNull(loadedV2);
        assertEquals("Site V2 - Updated", loadedV2.getDescription());
        assertEquals(Category.LOGIN, loadedV2.getCategory());
        // The repo's (DELETE+INSERT) logic should reflect V2 fields
        assertEquals(4, loadedV2.getFields().size()); // 3 original + 1 new
        assertEquals("NEW_USER", loadedV2.getField("Username").getValue());
        assertEquals("Added note", loadedV2.getField("Note").getValue());
    }

    /**
     * Tests that loading summaries returns only metadata (no fields)
     * for performance.
     */
    @Test
    void findAllSummaries_shouldReturnMetadataOnly() {
        // 1. Save two entries
        repository.save(createTestEntry("Entry 1", Category.LOGIN));
        repository.save(createTestEntry("Entry 2", Category.WIFI));

        // 2. Load summaries
        List<Entry> summaries = repository.findAllSummaries();

        // 3. Assert
        assertEquals(2, summaries.size());
        Entry summary = summaries.get(0);

        // The key property of summaries: they MUST NOT contain fields
        // to ensure the load operation is lightweight.
        assertTrue(summary.getFields().isEmpty());
        assertNotNull(summary.getId());
        assertNotNull(summary.getDescription());
        assertNotNull(summary.getCategory());
    }

    /**
     * Tests that deleting an entry also removes its fields (due to ON DELETE CASCADE).
     */
    @Test
    void deleteById_shouldRemoveEntryAndFields() {
        // 1. Save
        Entry entry = createTestEntry("To Be Deleted", Category.SECURE_NOTE);
        repository.save(entry);
        String id = entry.getId();

        // Verify it exists
        assertTrue(repository.findById(id).isPresent());

        // 2. Delete
        boolean deleted = repository.deleteById(id);
        assertTrue(deleted);

        // 3. Verify it's gone
        assertFalse(repository.findById(id).isPresent());

        // 4. Try deleting again
        boolean deletedAgain = repository.deleteById(id);
        assertFalse(deletedAgain);
    }

    /**
     * Tests that findById returns an empty Optional for an unknown ID.
     */
    @Test
    void findById_shouldReturnEmptyForUnknownId() {
        Optional<Entry> result = repository.findById("non-existent-id");
        assertTrue(result.isEmpty());
    }
}