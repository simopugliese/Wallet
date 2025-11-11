package com.simonepugliese.Core;

import com.simonepugliese.Model.Entry;
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
     * @return The fully decrypted Entry, ready for editing, or empty.
     */
    public Optional<Entry> loadAndDecryptEntry(String id) {
        log.info("Loading and decrypting entry (ID: {})...", id);
        // Load (data is encrypted)
        try {
            Optional<Entry> encryptedEntry = repository.findById(id);

            if (encryptedEntry.isEmpty()) {
                log.warn("No entry found for ID: {}", id);
                return Optional.empty();
            }
            log.debug("Entry found, decrypting...");
            return encryptedEntry.map(criptor::decrypt);
        } catch (Exception e) {
            log.error("Failed to load or decrypt entry (ID: {})", id, e);
            return Optional.empty();
        }
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