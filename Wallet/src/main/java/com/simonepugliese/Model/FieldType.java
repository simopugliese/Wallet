package com.simonepugliese.Model;

/**
 * Defines the semantic type of a {@link Field}.
 * <p>
 * This is used by the UI to determine how to render the field's value
 * (e.g., as masked text, a clickable link, or a simple text box).
 */
public enum FieldType {
    /**
     * A plain text field.
     */
    TEXT,

    /**
     * A text field whose value should be masked (e.g., "••••••••").
     */
    PASSWORD,

    /**
     * A URL, which the UI can render as a clickable link.
     */
    URL,

    /**
     * A date, which the UI can render using a date picker.
     */
    DATE,

    /**
     * A long-form, multi-line text field.
     */
    NOTE
}