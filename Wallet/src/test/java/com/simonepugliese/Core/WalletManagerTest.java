package com.simonepugliese.Core;

import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link WalletManager} (The Facade).
 *
 * <p>This test uses a "Mockist" approach (with simulated Mock classes)
 * to verify the COORDINATION LOGIC of the manager,
 * isolating it from the concrete implementations of IEntryRepository and ICriptor.</p>
 *
 * <p>We are verifying that the Manager calls the right methods in the right order.</p>
 */
class WalletManagerTest {

    // --- Mocks ---
    private MockEntryRepository mockRepository;
    private MockCriptor mockCriptor;
    private WalletManager manager;

    // --- Test Data ---
    private Entry plaintextEntry;
    private Entry encryptedEntry;

    @BeforeEach
    void setup() {
        // Initialize mocks and test data for each test
        mockRepository = new MockEntryRepository();
        mockCriptor = new MockCriptor();
        manager = new WalletManager(mockRepository, mockCriptor);

        plaintextEntry = new Entry("Test", Category.LOGIN);
        encryptedEntry = new Entry("Test", Category.LOGIN);

        // Simulate the relationship between the two
        mockCriptor.plaintext = plaintextEntry;
        mockCriptor.encrypted = encryptedEntry;
        mockRepository.entryToReturn = encryptedEntry;
    }

    /**
     * Verifies that saveEntry() calls encrypt() THEN save().
     */
    @Test
    void saveEntry_shouldCallEncrypt_then_Save() {
        // Action
        manager.saveEntry(plaintextEntry);

        // Assert
        // 1. Must have called encrypt()
        assertEquals(1, mockCriptor.encryptCallCount);
        // 2. The entry passed to encrypt must be the plaintext one
        assertSame(plaintextEntry, mockCriptor.lastEntryEncrypted);

        // 3. Must have called save()
        assertEquals(1, mockRepository.saveCallCount);
        // 4. The entry passed to save must be the encrypted one (returned from encrypt)
        assertSame(encryptedEntry, mockRepository.lastEntrySaved);
    }

    /**
     * Verifies that loadAndDecryptEntry() calls findById() THEN decrypt().
     */
    @Test
    void loadAndDecryptEntry_shouldCallFindById_then_Decrypt() {
        String testId = "123";

        // Action
        Optional<Entry> result = manager.loadAndDecryptEntry(testId);

        // Assert
        // 1. Must have called findById()
        assertEquals(1, mockRepository.findByIdCallCount);
        assertEquals(testId, mockRepository.lastIdLoaded);

        // 2. Must have called decrypt() (because findById returned an entry)
        assertEquals(1, mockCriptor.decryptCallCount);
        // 3. The entry passed to decrypt must be the encrypted one
        assertSame(encryptedEntry, mockCriptor.lastEntryDecrypted);

        // 4. The final result must be the decrypted entry
        assertTrue(result.isPresent());
        assertSame(plaintextEntry, result.get());
    }

    /**
     * Verifies that no decryption is attempted if the entry is not found.
     */
    @Test
    void loadAndDecryptEntry_shouldReturnEmpty_if_NotFound() {
        // Configure mock to find nothing
        mockRepository.entryToReturn = null;

        // Action
        Optional<Entry> result = manager.loadAndDecryptEntry("404");

        // Assert
        // 1. It tried to load
        assertEquals(1, mockRepository.findByIdCallCount);
        // 2. It MUST NOT call decrypt
        assertEquals(0, mockCriptor.decryptCallCount);
        // 3. The result must be empty
        assertTrue(result.isEmpty());
    }

    /**
     * Verifies that loading summaries only interacts with the repository
     * and performs NO decryption.
     */
    @Test
    void loadAllEntrySummaries_shouldCallRepository_and_NOT_Decrypt() {
        // Action
        manager.loadAllEntrySummaries();

        // Assert
        // 1. Must have called the repository
        assertEquals(1, mockRepository.findAllSummariesCallCount);
        // 2. Must NOT call the Criptor (it's non-sensitive)
        assertEquals(0, mockCriptor.decryptCallCount);
        assertEquals(0, mockCriptor.encryptCallCount);
    }

    /**
     * Verifies that deleting an entry only calls the repository.
     */
    @Test
    void deleteEntry_shouldCallRepository() {
        String testId = "abc";

        // Action
        manager.deleteEntry(testId);

        // Assert
        assertEquals(1, mockRepository.deleteByIdCallCount);
        assertEquals(testId, mockRepository.lastIdDeleted);
        // No crypto operations needed
        assertEquals(0, mockCriptor.decryptCallCount);
        assertEquals(0, mockCriptor.encryptCallCount);
    }


    // --- Internal Mock Classes for Testing ---

    /**
     * Mock implementation of {@link ICriptor} that tracks calls.
     */
    static class MockCriptor implements ICriptor {
        int encryptCallCount = 0;
        int decryptCallCount = 0;
        Entry lastEntryEncrypted = null;
        Entry lastEntryDecrypted = null;

        // Simulate transformation
        Entry plaintext;
        Entry encrypted;

        @Override
        public Entry encrypt(Entry entry) {
            encryptCallCount++;
            lastEntryEncrypted = entry;
            return encrypted; // Return the simulated encrypted version
        }

        @Override
        public Entry decrypt(Entry entry) {
            decryptCallCount++;
            lastEntryDecrypted = entry;
            return plaintext; // Return the simulated decrypted version
        }
    }

    /**
     * Mock implementation of {@link IEntryRepository} that tracks calls.
     */
    static class MockEntryRepository implements IEntryRepository {
        int saveCallCount = 0;
        int findByIdCallCount = 0;
        int findAllSummariesCallCount = 0;
        int deleteByIdCallCount = 0;

        Entry lastEntrySaved = null;
        String lastIdLoaded = null;
        String lastIdDeleted = null;

        // Simulated data
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
            return List.of(); // Result doesn't matter, only the call
        }

        @Override
        public boolean deleteById(String id) {
            deleteByIdCallCount++;
            lastIdDeleted = id;
            return true;
        }
    }
}