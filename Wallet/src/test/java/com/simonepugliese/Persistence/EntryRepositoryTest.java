package com.simonepugliese.Persistence;

import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Model.Field;
import com.simonepugliese.Model.FieldType;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di integrazione per {@link EntryRepository}.
 * <p>
 * Questo test utilizza un database SQLite in-memory (":memory:")
 * per testare le operazioni CRUD (Create, Read, Update, Delete)
 * in modo isolato e veloce.
 * <p>
 * Verifica:
 * 1. Connessione in-memory.
 * 2. Salvataggio di una nuova Entry e dei suoi Fields.
 * 3. Caricamento di una Entry completa tramite ID.
 * 4. Aggiornamento (UPSERT) di una Entry e dei suoi Fields.
 * 5. Caricamento di tutti i riepiloghi (summaries) senza Fields.
 * 6. Cancellazione di una Entry.
 */
class EntryRepositoryTest {

    private static EntryRepository repository;
    private static DbConnector dbConnector;

    @BeforeAll
    static void setupDatabase() {
        // Configura il DbConnector per usare un DB SQLite in-memory
        // Questo è Veloce, Isolato e non lascia file sul disco.
        DbConnector.setJdbcUrl("jdbc:sqlite::memory:");
        dbConnector = DbConnector.getInstance(); // Questo inizializza le tabelle
        repository = new EntryRepository();
    }

    /**
     * Pulisce le tabelle prima di OGNI test per garantire l'isolamento.
     */
    @BeforeEach
    void cleanupTables() {
        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Pulisce prima i campi (a causa della Foreign Key)
            stmt.executeUpdate("DELETE FROM Fields");
            // Poi pulisce le entries
            stmt.executeUpdate("DELETE FROM Entries");
        } catch (Exception e) {
            fail("Pulizia DB fallita", e);
        }
    }

    /**
     * Crea una Entry di test complessa.
     */
    private Entry createTestEntry(String description, Category category) {
        Entry entry = new Entry(description, category);
        entry.putField("Username", new Field("user@test.com", FieldType.TEXT, false));
        entry.putField("Password", new Field("encrypted-pass-value", FieldType.PASSWORD, true));
        entry.putField("URL", new Field("https://test.com", FieldType.URL, false));
        return entry;
    }

    @Test
    void save_and_findById_shouldReturnSavedEntryWithFields() {
        Entry entry = createTestEntry("Google", Category.LOGIN);

        // 1. Salva
        repository.save(entry);

        // 2. Leggi
        Optional<Entry> loadedOpt = repository.findById(entry.getId());

        // 3. Assert
        assertTrue(loadedOpt.isPresent());
        Entry loaded = loadedOpt.get();

        assertEquals(entry.getId(), loaded.getId());
        assertEquals("Google", loaded.getDescription());
        assertEquals(Category.LOGIN, loaded.getCategory());

        // Verifica i campi
        assertEquals(3, loaded.getFields().size());
        assertEquals("user@test.com", loaded.getField("Username").getValue());
        assertEquals("encrypted-pass-value", loaded.getField("Password").getValue());
        assertTrue(loaded.getField("Password").isSensitive());
    }

    @Test
    void save_shouldUpdateExistingEntry_OnConflict() {
        // 1. Crea e salva la versione 1
        Entry entryV1 = createTestEntry("Sito V1", Category.GENERIC);
        repository.save(entryV1);

        // 2. Modifica la entry (stesso ID)
        entryV1.setDescription("Sito V2 - Aggiornato");
        entryV1.setCategory(Category.LOGIN);
        // Modifica un campo e aggiungine uno nuovo
        entryV1.putField("Username", new Field("NUOVO_USER", FieldType.TEXT, false));
        entryV1.putField("Note", new Field("Nota aggiunta", FieldType.NOTE, false));

        // 3. Salva di nuovo (questo è un UPSERT)
        repository.save(entryV1);

        // 4. Ricarica e verifica
        Entry loadedV2 = repository.findById(entryV1.getId()).orElse(null);
        assertNotNull(loadedV2);
        assertEquals("Sito V2 - Aggiornato", loadedV2.getDescription());
        assertEquals(Category.LOGIN, loadedV2.getCategory());
        // L'UPSERT del repository (DELETE+INSERT) deve riflettere i campi V2
        assertEquals(4, loadedV2.getFields().size()); // 3 originali + 1 nuovo
        assertEquals("NUOVO_USER", loadedV2.getField("Username").getValue());
        assertEquals("Nota aggiunta", loadedV2.getField("Note").getValue());
    }

    @Test
    void findAllSummaries_shouldReturnMetadataOnly() {
        // 1. Salva due entries
        repository.save(createTestEntry("Entry 1", Category.LOGIN));
        repository.save(createTestEntry("Entry 2", Category.WIFI));

        // 2. Carica i riepiloghi
        List<Entry> summaries = repository.findAllSummaries();

        // 3. Assert
        assertEquals(2, summaries.size());
        Entry summary = summaries.get(0);

        // La proprietà chiave dei riepiloghi: NON devono avere campi caricati
        // per essere veloci.
        assertTrue(summary.getFields().isEmpty());
        assertNotNull(summary.getId());
        assertNotNull(summary.getDescription());
        assertNotNull(summary.getCategory());
    }

    @Test
    void deleteById_shouldRemoveEntryAndFields() {
        // 1. Salva
        Entry entry = createTestEntry("Da Cancellare", Category.SECURE_NOTE);
        repository.save(entry);
        String id = entry.getId();

        // Verifica che esista
        assertTrue(repository.findById(id).isPresent());

        // 2. Cancella
        boolean deleted = repository.deleteById(id);
        assertTrue(deleted);

        // 3. Verifica che sia sparita
        assertFalse(repository.findById(id).isPresent());

        // 4. Prova a cancellare di nuovo
        boolean deletedAgain = repository.deleteById(id);
        assertFalse(deletedAgain);
    }

    @Test
    void findById_shouldReturnEmptyForUnknownId() {
        Optional<Entry> result = repository.findById("id-non-esistente");
        assertTrue(result.isEmpty());
    }
}