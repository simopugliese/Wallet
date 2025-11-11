// File: src/test/java/com/simonepugliese/Core/WalletManagerTest.java

package com.simonepugliese.Core;

import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Security.DecryptionFailedException; // Import aggiunto
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Function; // Import aggiunto

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test per WalletManager (La Facade).
 *
 * <p>Questo test usa un approccio "Mockist" per verificare la
 * LOGICA DI COORDINAZIONE del manager, isolandolo dalle
 * implementazioni concrete di IEntryRepository e ICriptor.</p>
 */
class WalletManagerTest {

    // --- Mocks ---
    private MockEntryRepository mockRepository;
    private MockCriptor mockCriptor;
    private WalletManager manager;

    // --- Dati di Test ---
    private Entry plaintextEntry;
    private Entry encryptedEntry;

    @BeforeEach
    void setup() {
        mockRepository = new MockEntryRepository();
        mockCriptor = new MockCriptor();
        manager = new WalletManager(mockRepository, mockCriptor);

        plaintextEntry = new Entry("Test", Category.LOGIN);
        encryptedEntry = new Entry("Test", Category.LOGIN);

        mockCriptor.plaintext = plaintextEntry;
        mockCriptor.encrypted = encryptedEntry;
        mockRepository.entryToReturn = encryptedEntry;
    }

    /**
     * SCENARIO 1: Verifica che saveEntry() chiami prima encrypt() e POI save().
     */
    @Test
    void saveEntry_shouldCallEncrypt_then_Save() {
        // Azione
        manager.saveEntry(plaintextEntry);

        // Assert
        assertEquals(1, mockCriptor.encryptCallCount, "encrypt() deve essere chiamato 1 volta");
        assertSame(plaintextEntry, mockCriptor.lastEntryEncrypted, "L'entry da cifrare è quella in chiaro");
        assertEquals(1, mockRepository.saveCallCount, "save() deve essere chiamato 1 volta");
        assertSame(encryptedEntry, mockRepository.lastEntrySaved, "L'entry da salvare è quella cifrata");
    }

    /**
     * SCENARIO 2: Verifica che loadAndDecryptEntry() chiami prima findById() e POI decrypt().
     */
    @Test
    void loadAndDecryptEntry_shouldCallFindById_then_Decrypt() {
        String testId = "123";

        // Azione
        Optional<Entry> result = manager.loadAndDecryptEntry(testId);

        // Assert
        assertEquals(1, mockRepository.findByIdCallCount, "findById() deve essere chiamato 1 volta");
        assertEquals(testId, mockRepository.lastIdLoaded);
        assertEquals(1, mockCriptor.decryptCallCount, "decrypt() deve essere chiamato 1 volta");
        assertSame(encryptedEntry, mockCriptor.lastEntryDecrypted, "L'entry da decifrare è quella cifrata");
        assertTrue(result.isPresent());
        assertSame(plaintextEntry, result.get(), "Il risultato deve essere l'entry in chiaro");
    }

    /**
     * SCENARIO 3 (Cattivo): Verifica che decrypt() NON venga chiamato
     * se l'entry non viene trovata.
     */
    @Test
    void loadAndDecryptEntry_shouldReturnEmpty_if_NotFound() {
        // Configura il mock per non trovare nulla
        mockRepository.entryToReturn = null;

        // Azione
        Optional<Entry> result = manager.loadAndDecryptEntry("404");

        // Assert
        assertEquals(1, mockRepository.findByIdCallCount);
        assertEquals(0, mockCriptor.decryptCallCount, "Decrypt NON deve essere chiamato se l'entry non è trovata");
        assertTrue(result.isEmpty());
    }

    /**
     * SCENARIO 4: Verifica che il caricamento dei riepiloghi
     * chiami solo il repository e MAI il criptor.
     */
    @Test
    void loadAllEntrySummaries_shouldCallRepository_and_NOT_Decrypt() {
        // Azione
        manager.loadAllEntrySummaries();

        // Assert
        assertEquals(1, mockRepository.findAllSummariesCallCount);
        assertEquals(0, mockCriptor.decryptCallCount, "Il criptor non deve essere usato per i riepiloghi");
        assertEquals(0, mockCriptor.encryptCallCount, "Il criptor non deve essere usato per i riepiloghi");
    }

    /**
     * SCENARIO 5: Verifica che deleteEntry() chiami solo il repository.
     */
    @Test
    void deleteEntry_shouldCallRepository() {
        String testId = "abc";

        // Azione
        manager.deleteEntry(testId);

        // Assert
        assertEquals(1, mockRepository.deleteByIdCallCount);
        assertEquals(testId, mockRepository.lastIdDeleted);
        assertEquals(0, mockCriptor.decryptCallCount);
        assertEquals(0, mockCriptor.encryptCallCount);
    }

    /**
     * SCENARIO 6 (Cattivo - CRITICO): Verifica che se il decrypt fallisce
     * (es. password errata), l'eccezione si propaghi correttamente.
     */
    @Test
    void loadAndDecryptEntry_shouldPropagateDecryptionFailedException() {
        // Setup: Il repository TROVA l'entry cifrata
        mockRepository.entryToReturn = encryptedEntry;

        // Setup: Il criptor FALLISCE (lancia l'eccezione)
        // Iniettiamo il comportamento di fallimento nel mock
        mockCriptor.decryptBehavior = (entry) -> {
            throw new DecryptionFailedException("Simulazione password errata", null);
        };

        // Azione e Assert: Verifica che il WalletManager propaghi l'eccezione
        assertThrows(DecryptionFailedException.class, () -> manager.loadAndDecryptEntry("123"), "Il WalletManager deve propagare la DecryptionFailedException dal Criptor");

        // Verifica che il manager abbia effettivamente provato a decifrare
        assertEquals(1, mockCriptor.decryptCallCount, "Deve aver tentato di decifrare");
    }


    // --- Classi Mock Interne per il Test ---

    /**
     * Mock di ICriptor che traccia le chiamate.
     * MODIFICATO: Ora supporta un comportamento di decrypt personalizzato.
     */
    static class MockCriptor implements ICriptor {
        int encryptCallCount = 0;
        int decryptCallCount = 0;
        Entry lastEntryEncrypted = null;
        Entry lastEntryDecrypted = null;

        // Simula la trasformazione
        Entry plaintext;
        Entry encrypted;

        // Campo per iniettare comportamento "cattivo"
        Function<Entry, Entry> decryptBehavior = null;

        @Override
        public Entry encrypt(Entry entry) {
            encryptCallCount++;
            lastEntryEncrypted = entry;
            return encrypted; // Ritorna la versione cifrata simulata
        }

        @Override
        public Entry decrypt(Entry entry) {
            decryptCallCount++;
            lastEntryDecrypted = entry;

            // Se un comportamento è stato iniettato, usalo
            if (decryptBehavior != null) {
                return decryptBehavior.apply(entry);
            }
            // Altrimenti, usa il comportamento di default
            return plaintext;
        }
    }

    /**
     * Mock di IEntryRepository che traccia le chiamate.
     */
    static class MockEntryRepository implements IEntryRepository {
        int saveCallCount = 0;
        int findByIdCallCount = 0;
        int findAllSummariesCallCount = 0;
        int deleteByIdCallCount = 0;

        Entry lastEntrySaved = null;
        String lastIdLoaded = null;
        String lastIdDeleted = null;

        // Dati simulati
        Entry entryToReturn = null;

        @Override
        public void save(Entry entry) {
            saveCallCount++;
            lastEntrySaved = entry;
        }

        @Override
        public Optional<Entry> findById(String id) {
            findByIdCallCount++;
            lastIdLoaded = id;
            return Optional.ofNullable(entryToReturn);
        }

        @Override
        public List<Entry> findAllSummaries() {
            findAllSummariesCallCount++;
            return List.of(); // Il risultato non importa, solo la chiamata
        }

        @Override
        public boolean deleteById(String id) {
            deleteByIdCallCount++;
            lastIdDeleted = id;
            return true;
        }
    }
}