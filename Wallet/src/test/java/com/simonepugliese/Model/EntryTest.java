package com.simonepugliese.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the {@link Entry} model class.
 *
 * <p>Verifies the core logic of the Entry, such as field management,
 * immutability, and validation.</p>
 */
class EntryTest {

    private Entry entry;
    private Field field1;

    @BeforeEach
    void setup() {
        entry = new Entry("Test Entry", Category.GENERIC);
        field1 = new Field("value1", FieldType.TEXT, false);
    }

    /**
     * Tests that a field is correctly added and retrieved.
     */
    @Test
    void putField_and_getField_shouldWork() {
        // Action
        entry.putField("field1", field1);

        // Assert
        Field retrieved = entry.getField("field1");
        assertSame(field1, retrieved);
        assertEquals("value1", retrieved.getValue());
    }

    /**
     * Tests that removing a field works correctly.
     */
    @Test
    void removeField_shouldRemoveExistingField() {
        entry.putField("field1", field1);
        assertTrue(entry.getFields().containsKey("field1"));

        // Action
        Field removed = entry.removeField("field1");

        // Assert
        assertSame(field1, removed);
        assertFalse(entry.getFields().containsKey("field1"));
        assertNull(entry.getField("field1"));
    }

    /**
     * Tests that putField throws an exception for null arguments,
     * as defined in its contract.
     */
    @Test
    void putField_shouldThrowException_forNullArgs() {
        assertThrows(IllegalArgumentException.class, () -> entry.putField(null, field1), "Putting a field with a null name should throw");

        assertThrows(IllegalArgumentException.class, () -> entry.putField("fieldName", null), "Putting a null field should throw");
    }

    /**
     * Tests that the map returned by getFields() is unmodifiable.
     */
    @Test
    void getFields_shouldReturnUnmodifiableMap() {
        entry.putField("field1", field1);
        Map<String, Field> fieldsMap = entry.getFields();

        // Assert
        assertThrows(UnsupportedOperationException.class, () -> fieldsMap.put("newField", new Field("val", FieldType.TEXT, false)), "The map returned by getFields() must be unmodifiable");
    }

    /**
     * Tests that fields are stored in insertion order (a key feature
     * of LinkedHashMap).
     */
    @Test
    void getFields_shouldPreserveInsertionOrder() {
        entry.putField("B-Field", new Field("b", FieldType.TEXT, false));
        entry.putField("C-Field", new Field("c", FieldType.TEXT, false));
        entry.putField("A-Field", new Field("a", FieldType.TEXT, false));

        // Get the keys as a list
        List<String> keys = entry.getFields().keySet().stream().toList();

        // Assert that the order is B, C, A (insertion order)
        // and not A, B, C (alphabetical order)
        assertEquals("B-Field", keys.get(0));
        assertEquals("C-Field", keys.get(1));
        assertEquals("A-Field", keys.get(2));
    }
}