package com.simonepugliese.Model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a single, flexible Entry in the wallet.
 * <p>
 * This class replaces the entire rigid Item hierarchy (e.g., LoginItem,
 * CreditCardItem) by using composition ("has-a" Fields) instead of
 * inheritance ("is-a" Item).
 * <p>
 * It acts as a container for metadata (description, category) and a
 * collection of {@link Field} objects.
 */
public final class Entry { // 'final' prevents inheritance

    /**
     * The unique, persistent identifier for this entry.
     */
    private final String id;

    /**
     * The user-friendly display name (e.g., "Facebook", "Work Wifi").
     */
    private String description;

    /**
     * The {@link Category} for grouping and icon display.
     */
    private Category category;

    /**
     * The collection of fields for this entry.
     * We use {@link LinkedHashMap} to achieve two goals:
     * 1. Fast O(1) lookup by field name (e.g., "Username").
     * 2. Preservation of insertion order for stable UI rendering.
     */
    private final Map<String, Field> fields;

    /**
     * Creates a new Entry with a generated UUID.
     * This constructor is for creating new, unsaved entries from the UI.
     *
     * @param description The user-friendly name (e.g., "Google").
     * @param category    The {@link Category} (e.g., LOGIN).
     */
    public Entry(String description, Category category) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.category = category;
        this.fields = new LinkedHashMap<>();
    }

    /**
     * Reconstructs an existing Entry from persistence.
     * This constructor is public to be accessible by the Saver/Repository
     * logic, which will reside in a different package (e.g., .persistence).
     *
     * @param id          The existing UUID from the database.
     * @param description The description from the database.
     * @param category    The category from the database.
     */
    public Entry(String id, String description, Category category) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.fields = new LinkedHashMap<>();
    }


    // --- Public API for Metadata ---

    /**
     * @return The unique identifier (UUID) of this entry.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The user-friendly display name.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The new display name.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The {@link Category} of this entry.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @param category The new {@link Category}.
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    // --- Public API for Field Management ---

    /**
     * Adds or updates a field in the entry.
     * If a field with the same name already exists, it will be overwritten.
     *
     * @param name  The name of the field (e.g., "Username"), used as the key.
     * @param field The {@link Field} object containing the value, type, and sensitivity.
     */
    public void putField(String name, Field field) {
        if (name == null || field == null) {
            throw new IllegalArgumentException("Field name and field cannot be null");
        }
        this.fields.put(name, field);
    }

    /**
     * Retrieves a single field by its name.
     *
     * @param name The name of the field (e.g., "Password").
     * @return The {@link Field} object, or {@code null} if no field
     * with that name exists.
     */
    public Field getField(String name) {
        return this.fields.get(name);
    }

    /**
     * Removes a field from the entry by its name.
     *
     * @param name The name of the field to remove.
     * @return The {@link Field} object that was removed, or {@code null} if
     * no field with that name was found.
     */
    public Field removeField(String name) {
        return this.fields.remove(name);
    }

    /**
     * Returns an unmodifiable view of the fields map.
     * This allows external classes (like the UI) to iterate over the fields
     * in their correct order without being able to modify the collection directly.
     *
     * @return An unmodifiable {@link Map} of the fields.
     */
    public Map<String, Field> getFields() {
        return Collections.unmodifiableMap(this.fields);
    }
}