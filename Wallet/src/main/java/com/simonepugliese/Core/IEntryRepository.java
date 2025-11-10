package com.simonepugliese.Core;

import com.simonepugliese.Model.Entry;
import java.util.List;
import java.util.Optional;

/**
 * Public contract for wallet data persistence.
 * This interface abstracts all storage operations (CRUD) for Entries.
 * <p>
 * This replaces the old Saver abstract class.
 * All methods in this repository are expected to handle *encrypted* data.
 * Decryption/Encryption is the responsibility of the WalletManager Facade.
 */
public interface IEntryRepository {

    /**
     * Saves a new Entry or updates an existing one in persistence.
     * This method assumes the Entry's sensitive fields are already encrypted.
     *
     * @param entry The Entry object to save (e.g., from an ICriptor).
     */
    void save(Entry entry);

    /**
     * Loads a single Entry by its unique ID, including all its Fields.
     * The returned Entry will contain *encrypted* values for sensitive fields.
     *
     * @param id The unique ID of the Entry to retrieve.
     * @return An Optional containing the Entry if found, or an empty Optional.
     */
    Optional<Entry> findById(String id);

    /**
     * Loads all Entries from persistence, but *only* their metadata
     * (id, description, category). This is a lightweight operation
     * for populating the main list view.
     *
     * @return A List of all Entries without their fields.
     */
    List<Entry> findAllSummaries();

    /**
     * Deletes an Entry (and all its associated fields) from persistence
     * using its unique ID.
     *
     * @param id The unique ID of the Entry to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    boolean deleteById(String id);
}