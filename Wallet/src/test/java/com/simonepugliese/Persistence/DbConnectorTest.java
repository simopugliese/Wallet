// File: src/test/java/com/simonepugliese/Persistence/DbConnectorTest.java

package com.simonepugliese.Persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
// Import aggiunti per l'eliminazione del file
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per il DbConnector.
 * Verifica il pattern Singleton (anche in multithreading) e l'inizializzazione.
 */
class DbConnectorTest {

    /**
     * Resettiamo il Singleton E CANCELLIAMO IL DB
     * prima di ogni test per garantire isolamento totale.
     * Questo costringe 'getInstance()' a rieseguire
     * 'initializeDatabase()' ogni volta.
     */
    @BeforeEach
    void resetSingletonAndDatabaseFile() {
        // 1. Resetta il singleton (necessario per testare l'init)
        try {
            Field instanceField = DbConnector.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            fail("Fallito reset del Singleton via reflection", e);
        }

        // 2. Elimina fisicamente il file DB per forzare la ri-creazione
        try {
            Files.deleteIfExists(Paths.get("wallet.db"));
        } catch (Exception e) {
            fail("Fallita eliminazione del file wallet.db", e);
        }
    }

    /**
     * SCENARIO 1 (Happy Path): getInstance() ritorna la stessa istanza.
     */
    @Test
    void getInstance_shouldReturnSameInstance_inSerialCalls() {
        DbConnector instance1 = DbConnector.getInstance();
        DbConnector instance2 = DbConnector.getInstance();

        assertNotNull(instance1, "Istanza 1 non deve essere null");
        assertNotNull(instance2, "Istanza 2 non deve essere null");
        assertSame(instance1, instance2, "Le istanze devono essere le stesse (assertSame)");
    }

    /**
     * SCENARIO 2 (Schema Init): Verifica che il costruttore (chiamato
     * da getInstance) crei le tabelle su un file DB pulito.
     */
    @Test
    void initializeDatabase_shouldCreateTables_onFirstGetInstance() {
        // Azione: la prima chiamata a getInstance() triggera il costruttore
        // e la creazione del file wallet.db
        DbConnector connector = DbConnector.getInstance();
        assertNotNull(connector);

        // Assert: verifichiamo che le tabelle esistano
        try (Connection conn = connector.getConnection()) {
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "Entries", null)) {
                assertTrue(rs.next(), "Tabella 'Entries' deve esistere");
            }
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "Fields", null)) {
                assertTrue(rs.next(), "Tabella 'Fields' deve esistere");
            }
        } catch (Exception e) {
            fail("Errore durante la verifica delle tabelle", e);
        }
    }

    /**
     * SCENARIO 3 (Cattivo - Thread Safety): Verifichiamo che 100 thread
     * che chiamano getInstance() contemporaneamente ottengano TUTTI
     * la STESSA identica istanza (prova la correttezza del double-check locking).
     */
    @Test
    void getInstance_shouldReturnSameInstance_inConcurrentCalls() throws InterruptedException {
        int numThreads = 100;
        Set<DbConnector> instances = Collections.newSetFromMap(new ConcurrentHashMap<>());

        var executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                instances.add(DbConnector.getInstance());
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Assert
        assertEquals(1, instances.size(), "Tutti i thread devono ricevere la stessa istanza");
    }
}