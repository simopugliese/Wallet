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
 * Integration test per EntryRepository.
 *
 * <p>NOTA: A seguito del refactor del DbConnector, questo test
 * ora opera sul file DB di default ("wallet.db").
 * L'annotazione @BeforeEach (cleanupTables) è FONDAMENTALE
 * per garantire l'isolamento tra i test.</p>
 */
class EntryRepositoryTest {

    private static EntryRepository repository;
    private static DbConnector dbConnector;

    /**
     * Configura il DbConnector e il Repository UNA VOLTA per tutti i test.
     */
    @BeforeAll
    static void setupDatabase() {
        // Resettare il singleton non è più necessario se non
        // abbiamo altri test che sporcano lo stato (come DbConnectorTest)
        // Se DbConnectorTest esiste, assicurati che usi @BeforeEach
        // per resettare il singleton come abbiamo discusso.

        // Otteniamo il DbConnector e creiamo il Repository
        dbConnector = DbConnector.getInstance();
        repository = new EntryRepository();
    }

    /**
     * Pulisce le tabelle PRIMA DI OGNI TEST.
     * Questo garantisce che ogni test parta da un DB pulito (isolamento).
     */
    @BeforeEach
    void cleanupTables() {
        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Puliamo Fields prima a causa della Foreign Key
            stmt.executeUpdate("DELETE FROM Fields");
            stmt.executeUpdate("DELETE FROM Entries");
        } catch (Exception e) {
            fail("La pulizia del DB è fallita", e);
        }
    }

    /**
     * Helper per creare un'Entry di test complessa.
     */
    private Entry createTestEntry(String description, Category category) {
        Entry entry = new Entry(description, category);
        entry.putField("Username", new Field("user@test.com", FieldType.TEXT, false));
        // Nota: il repository salva i valori come li riceve.
        // Non gli interessa se sono cifrati o no.
        entry.putField("Password", new Field("valore-cifrato-simulato", FieldType.PASSWORD, true));
        entry.putField("URL", new Field("https://test.com", FieldType.URL, false));
        return entry;
    }

    /**
     * SCENARIO 1 (CRUD): Ciclo completo Save -> FindById
     */
    @Test
    void save_and_findById_shouldReturnSavedEntryWithFields() {
        Entry entry = createTestEntry("Google", Category.LOGIN);

        // 1. Save
        repository.save(entry);

        // 2. Read
        Optional<Entry> loadedOpt = repository.findById(entry.getId());

        // 3. Assert
        assertTrue(loadedOpt.isPresent(), "L'entry salvata deve essere trovata");
        Entry loaded = loadedOpt.get();

        assertEquals(entry.getId(), loaded.getId());
        assertEquals("Google", loaded.getDescription());
        assertEquals(Category.LOGIN, loaded.getCategory());

        // Verifica i campi
        assertEquals(3, loaded.getFields().size(), "Devono esserci 3 campi");
        assertEquals("user@test.com", loaded.getField("Username").getValue());
        assertEquals("valore-cifrato-simulato", loaded.getField("Password").getValue());
        assertTrue(loaded.getField("Password").isSensitive());
    }

    /**
     * SCENARIO 2 (UPSERT): Verifica che un 'save' su un ID esistente
     * aggiorni correttamente l'Entry e i suoi Field.
     */
    @Test
    void save_shouldUpdateExistingEntry_OnConflict() {
        // 1. Crea e salva Versione 1
        Entry entryV1 = createTestEntry("Sito V1", Category.GENERIC);
        repository.save(entryV1);

        // 2. Modifica l'entry (stesso ID)
        entryV1.setDescription("Sito V2 - Aggiornato");
        entryV1.setCategory(Category.LOGIN);
        // Modifica un campo e aggiungine uno nuovo
        entryV1.putField("Username", new Field("NUOVO_USER", FieldType.TEXT, false));
        entryV1.putField("Note", new Field("Nota aggiunta", FieldType.NOTE, false));

        // 3. Salva di nuovo (questo deve triggerare un UPDATE)
        repository.save(entryV1);

        // 4. Ricarica e verifica
        Entry loadedV2 = repository.findById(entryV1.getId()).orElse(null);
        assertNotNull(loadedV2, "L'entry V2 non deve essere null");
        assertEquals("Sito V2 - Aggiornato", loadedV2.getDescription());
        assertEquals(Category.LOGIN, loadedV2.getCategory());
        // La logica (DELETE+INSERT) del repository deve riflettere i campi V2
        assertEquals(4, loadedV2.getFields().size(), "Ora ci devono essere 4 campi");
        assertEquals("NUOVO_USER", loadedV2.getField("Username").getValue());
        assertEquals("Nota aggiunta", loadedV2.getField("Note").getValue());
    }

    /**
     * SCENARIO 3 (Read Summaries): Verifica che findAllSummaries
     * carichi solo i metadati (e NESSUN campo).
     */
    @Test
    void findAllSummaries_shouldReturnMetadataOnly() {
        // 1. Salva due entry
        repository.save(createTestEntry("Entry 1", Category.LOGIN));
        repository.save(createTestEntry("Entry 2", Category.WIFI));

        // 2. Carica i riepiloghi
        List<Entry> summaries = repository.findAllSummaries();

        // 3. Assert
        assertEquals(2, summaries.size(), "Devono essere trovati 2 riepiloghi");
        Entry summary = summaries.get(0); // Basta controllarne uno

        // La proprietà chiave dei riepiloghi: NON DEVONO avere campi
        // per essere leggeri da caricare.
        assertTrue(summary.getFields().isEmpty(), "I riepiloghi non devono contenere campi");
        assertNotNull(summary.getId());
        assertNotNull(summary.getDescription());
        assertNotNull(summary.getCategory());
    }

    /**
     * SCENARIO 4 (Delete): Verifica che deleteById rimuova l'Entry
     * e che i successivi find/delete falliscano.
     */
    @Test
    void deleteById_shouldRemoveEntryAndFields() {
        // 1. Salva
        Entry entry = createTestEntry("Da Cancellare", Category.SECURE_NOTE);
        repository.save(entry);
        String id = entry.getId();

        // Verifica che esista
        assertTrue(repository.findById(id).isPresent(), "L'entry deve esistere prima della delete");

        // 2. Cancella
        boolean deleted = repository.deleteById(id);
        assertTrue(deleted, "deleteById deve ritornare true se ha successo");

        // 3. Verifica che sia sparita
        assertFalse(repository.findById(id).isPresent(), "L'entry non deve più esistere dopo la delete");

        // 4. Prova a cancellarla di nuovo (test "cattivo")
        boolean deletedAgain = repository.deleteById(id);
        assertFalse(deletedAgain, "deleteById deve ritornare false se l'ID non esiste");
    }

    /**
     * SCENARIO 5 (Read - Not Found): Verifica che findById
     * gestisca correttamente gli ID non esistenti.
     */
    @Test
    void findById_shouldReturnEmptyForUnknownId() {
        Optional<Entry> result = repository.findById("id-che-non-esiste-123");
        assertTrue(result.isEmpty(), "Un ID non esistente deve restituire Optional.empty");
    }
}