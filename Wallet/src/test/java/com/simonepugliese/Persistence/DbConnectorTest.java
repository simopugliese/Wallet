package com.simonepugliese.Persistence;

import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link DbConnector}.
 *
 * <p>This test focuses on the unit-testable behaviors:
 * <ol>
 * <li>Singleton pattern: {@code getInstance()} returns the same instance.</li>
 * <li>Configuration logic: {@code setJdbcUrl()} respects its constraints.</li>
 * <li>Schema Creation: A basic check that tables are created on init.</li>
 * </ol>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DbConnectorTest {

    // *** MODIFICA ***
    // Sostituito il DB in-memory con un file.
    // Quello in-memory veniva distrutto alla chiusura della connessione
    // nel costruttore, facendo fallire il test 'initializeDatabase'.
    private static final String SHARED_IN_MEMORY_PATH = "test_connector.db";

    /**
     * Resets the static Singleton field before each test to ensure isolation.
     * This is necessary for testing static Singleton initialization behavior.
     */
    @BeforeEach
    void resetSingleton() {
        try {
            // Use reflection to set the private static 'instance' field to null
            Field instanceField = DbConnector.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            fail("Failed to reset Singleton instance via reflection", e);
        }
    }

    /**
     * Tests that {@code setJdbcUrl()} throws an exception if called *after*
     * the Singleton instance has been created.
     */
    @Test
    @Order(1)
    void setJdbcUrl_shouldThrowException_ifCalledAfterGetInstance() {
        // 1. Ensure instance is created (using file db)
        DbConnector.setJdbcUrl(SHARED_IN_MEMORY_PATH); // <-- FIX
        DbConnector connector = DbConnector.getInstance();
        assertNotNull(connector);

        // 2. Try to set URL again
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> DbConnector.setJdbcUrl("another.db"), "Should not be able to set URL after instance is created");

        assertEquals("Cannot set URL after instance is created.", ex.getMessage());
    }

    /**
     * Tests that multiple calls to {@code getInstance()} return the exact
     * same object instance (Singleton pattern).
     */
    @Test
    @Order(2)
    void getInstance_shouldReturnSameInstance() {
        DbConnector.setJdbcUrl(SHARED_IN_MEMORY_PATH); // <-- FIX
        DbConnector instance1 = DbConnector.getInstance();
        DbConnector instance2 = DbConnector.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2, "getInstance() should always return the same Singleton instance");
    }

    /**
     * Verifies that the {@code initializeDatabase} method (called by the constructor)
     * successfully creates the expected tables and they persist across connections.
     */
    @Test
    @Order(3)
    void initializeDatabase_shouldCreateTables() {
        DbConnector.setJdbcUrl(SHARED_IN_MEMORY_PATH); // <-- FIX
        DbConnector connector = DbConnector.getInstance();
        assertNotNull(connector, "getInstance() should not return null");

        // Check if tables exist by querying schema metadata on a *new* connection
        try (Connection conn = connector.getConnection()) {
            assertNotNull(conn, "GetConnection should return a valid connection");
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "Entries", null)) {
                assertTrue(rs.next(), "Table 'Entries' should have been created and persist");
            }
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "Fields", null)) {
                assertTrue(rs.next(), "Table 'Fields' should have been created and persist");
            }
        } catch (Exception e) {
            fail("Failed querying metadata for tables", e);
        }
    }
}