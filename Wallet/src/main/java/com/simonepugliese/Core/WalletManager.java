package com.simonepugliese.Core;

import com.simonepugliese.Model.Entry;
import com.simonepugliese.Security.DecryptionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Main Facade for the wallet application.
 * <p>
 * This is the central entry point for all business logic, coordinating
 * the repository and criptor. It ensures that data is always
 * encrypted before saving and decrypted after loading.
 */
public final class WalletManager {

    private static final Logger log = LoggerFactory.getLogger(WalletManager.class);

    private final IEntryRepository repository;
    private final ICriptor criptor;

    /**
     * Constructs a new WalletManager.
     * The dependencies are injected via the public interfaces.
     *
     * @param repository The persistence strategy (e.g., EntryRepository).
     * @param criptor    The encryption strategy (e.g., EntryCriptor).
     */
    public WalletManager(IEntryRepository repository, ICriptor criptor) {
        this.repository = repository;
        this.criptor = criptor;
        log.info("WalletManager initialized.");
    }

    /**
     * Encrypts and saves a full Entry.
     *
     * @param entry The Entry in its plaintext state (from the UI).
     */
    public void saveEntry(Entry entry) {
        try {
            log.info("Saving entry (ID: {})...", entry.getId());
            // 1. Encrypt
            Entry encryptedEntry = criptor.encrypt(entry);
            // 2. Save
            repository.save(encryptedEntry);
            log.info("Entry saved successfully (ID: {}).", entry.getId());
            //TODO: (This is where you would notify Observers)
        } catch (Exception e) {
            log.error("Failed to save entry (ID: {})", entry.getId(), e);
        }
    }

    /**
     * Loads a single, fully-detailed Entry and decrypts it.
     *
     * @param id The unique ID of the entry to load.
     * @return The fully decrypted Entry, ready for editing, or an empty Optional
     * if the entry was not found or a DB error occurred.
     * @throws DecryptionFailedException if the entry is found but decryption fails
     * (e.g., due to an incorrect master password).
     */
    public Optional<Entry> loadAndDecryptEntry(String id) throws DecryptionFailedException {
        log.info("Loading and decrypting entry (ID: {})...", id);

        Optional<Entry> encryptedEntry;
        try {
            encryptedEntry = repository.findById(id);
        } catch (Exception e) {
            log.error("Failed to load entry from repository (ID: {})", id, e);
            return Optional.empty();
        }

        if (encryptedEntry.isEmpty()) {
            log.warn("No entry found for ID: {}", id);
            return Optional.empty();
        }
        log.debug("Entry found, decrypting...");
        return encryptedEntry.map(criptor::decrypt);
    }

    /**
     * Loads all Entry summaries (id, description, category) for display
     * in the main list. This data is not sensitive and needs no decryption.
     *
     * @return A List of all Entry summaries, an empty list in case of error
     */
    public List<Entry> loadAllEntrySummaries() {
        log.info("Loading all entry summaries...");
        try {
            return repository.findAllSummaries();
        } catch (Exception e) {
            log.error("Failed to load summaries", e);
            return List.of();
        }
    }

    /**
     * Deletes an Entry by its ID.
     *
     * @param id The unique ID of the Entry to delete.
     */
    public void deleteEntry(String id) {
        log.info("Deleting entry (ID: {})...", id);
        try {
            repository.deleteById(id);
            log.info("Entry deleted successfully (ID: {}).", id);
            //TODO: notify Observers
        } catch (Exception e) {
            log.error("Failed to delete entry (ID: {})", id, e);
        }
    }
}