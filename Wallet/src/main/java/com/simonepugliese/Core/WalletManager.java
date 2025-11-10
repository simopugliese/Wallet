package com.simonepugliese.Core;

import com.simonepugliese.Model.Entry;
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
    }

    /**
     * Encrypts and saves a full Entry.
     *
     * @param entry The Entry in its plaintext state (from the UI).
     */
    public void saveEntry(Entry entry) {
        // 1. Encrypt
        Entry encryptedEntry = criptor.encrypt(entry);
        // 2. Save
        repository.save(encryptedEntry);
        //TODO: (This is where you would notify Observers)
    }

    /**
     * Loads a single, fully-detailed Entry and decrypts it.
     *
     * @param id The unique ID of the entry to load.
     * @return The fully decrypted Entry, ready for editing, or empty.
     */
    public Optional<Entry> loadAndDecryptEntry(String id) {
        // 1. Load (data is encrypted)
        Optional<Entry> encryptedEntry = repository.findById(id);

        // 2. Decrypt (if present)
        return encryptedEntry.map(criptor::decrypt);
    }

    /**
     * Loads all Entry summaries (id, description, category) for display
     * in the main list. This data is not sensitive and needs no decryption.
     *
     * @return A List of all Entry summaries.
     */
    public List<Entry> loadAllEntrySummaries() {
        return repository.findAllSummaries();
    }

    /**
     * Deletes an Entry by its ID.
     *
     * @param id The unique ID of the Entry to delete.
     */
    public void deleteEntry(String id) {
        repository.deleteById(id);
        //TODO: (This is where you would notify Observers)
    }
}