package com.simonepugliese.Model;

/**
 * Represents a single piece of data within an {@link Entry}.
 * <p>
 * This class holds the value, type, and sensitivity, but not the name
 * (e.g., "Username"), as the name will be used as the key in the
 * {@link Entry}'s field map.
 */
public final class Field { // 'final' prevents inheritance

    /**
     * The actual data (e.g., "simone", "P4$$w0rd", "https://...").
     * This value will be encrypted by the Criptor if 'sensitive' is true.
     */
    private String value;

    /**
     * The semantic type of the field, guiding UI representation.
     */
    private FieldType type;

    /**
     * Flag indicating if this field's value must be encrypted at rest.
     * This is the flag the Criptor will check.
     */
    private boolean sensitive;

    /**
     * Constructs a new Field.
     *
     * @param value     The string value (e.g., the password, the username).
     * @param type      The {@link FieldType} (e.g., PASSWORD, TEXT).
     * @param sensitive True if this value should be encrypted, false otherwise.
     */
    public Field(String value, FieldType type, boolean sensitive) {
        this.value = value;
        this.type = type;
        this.sensitive = sensitive;
    }

    /**
     * Gets the field's value.
     * <p>
     * Note: If this field was loaded from persistence and is sensitive,
     * this will return the *encrypted* value until it is processed by
     * a Criptor.
     *
     * @return The raw string value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the field's value.
     * <p>
     * This method is intentionally public to be used by the Criptor
     * strategies (to set the encrypted/decrypted value) and the UI.
     *
     * @param value The new raw string value.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return The {@link FieldType} of this field.
     */
    public FieldType getType() {
        return type;
    }

    /**
     * Sets the {@link FieldType} of this field.
     *
     * @param type The new type.
     */
    public void setType(FieldType type) {
        this.type = type;
    }

    /**
     * @return True if this field is marked as sensitive, false otherwise.
     */
    public boolean isSensitive() {
        return sensitive;
    }

    /**
     * Sets the sensitivity flag for this field.
     *
     * @param sensitive The new sensitivity state.
     */
    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }
}