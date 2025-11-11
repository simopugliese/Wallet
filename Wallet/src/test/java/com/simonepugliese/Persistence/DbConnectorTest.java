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
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per il DbConnector.
 * Verifica il pattern Singleton (anche in multithreading) e l'inizializzazione.
 */
class DbConnectorTest {

    /**
     * Resettiamo il Singleton tra i test per garantire l'isolamento.
     * Questo è FONDAMENTALE per testare l'inizializzazione.
     */
    @BeforeEach
    void resetSingleton() {
        try {
            Field instanceField = DbConnector.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            fail("Fallito reset del Singleton via reflection", e);
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
     * da getInstance) crei le tabelle.
     */
    @Test
    void initializeDatabase_shouldCreateTables_onFirstGetInstance() {
        // Azione: la prima chiamata a getInstance() triggera il costruttore
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
        // Usiamo un Set thread-safe per collezionare le istanze.
        // Se il Singleton funziona, questo set conterrà UN SOLO elemento.
        Set<DbConnector> instances = Collections.newSetFromMap(new ConcurrentHashMap<>());

        var executor = Executors.newFixedThreadPool(numThreads);

        // Azione: 100 thread diversi chiamano getInstance()
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                instances.add(DbConnector.getInstance());
            });
        }

        // Aspettiamo che tutti i thread finiscano
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Assert
        assertEquals(1, instances.size(), "Tutti i thread devono ricevere la stessa istanza");
    }
}