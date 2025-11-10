package com.simonepugliese.Persistence;

import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Model.Field;
import com.simonepugliese.Model.FieldType;
import com.simonepugliese.Security.CryptoUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link EntryCriptor}.
 *
 * <p>This test verifies that the encryption strategy correctly applies
 * {@link CryptoUtils} only to fields marked as 'sensitive'.</p>
 */
class EntryCriptorTest {

    private EntryCriptor criptor;
    private static final String MASTER_PASS = "test_pass_criptor";
    private static final String USERNAME_VAL = "user@example.com";
    private static final String PASSWORD_VAL = "P4$$w0rd_S3gret4!";

    /**
     * Initializes CryptoUtils, as EntryCriptor depends on it.
     */
    @BeforeAll
    static void setupCrypto() {
        CryptoUtils.setMasterPassword(MASTER_PASS);
    }

    @BeforeEach
    void setup() {
        this.criptor = new EntryCriptor();
    }

    /**
     * Creates a test Entry with one sensitive and one non-sensitive field.
     *
     * @return A new Entry object for testing.
     */
    private Entry createTestEntry() {
        Entry entry = new Entry("Test Login", Category.LOGIN);
        // Non-sensitive field
        entry.putField("Username", new Field(USERNAME_VAL, FieldType.TEXT, false));
        // Sensitive field
        entry.putField("Password", new Field(PASSWORD_VAL, FieldType.PASSWORD, true));
        return entry;
    }

    /**
     * Tests that the encrypt method only encrypts fields marked as sensitive.
     */
    @Test
    void encrypt_shouldEncryptSensitiveFieldsOnly() {
        Entry entry = createTestEntry();

        // Action
        Entry encryptedEntry = criptor.encrypt(entry);

        // Assert
        Field usernameField = encryptedEntry.getField("Username");
        Field passwordField = encryptedEntry.getField("Password");

        // The non-sensitive field MUST remain plaintext
        assertEquals(USERNAME_VAL, usernameField.getValue());
        assertFalse(usernameField.isSensitive());

        // The sensitive field MUST NOT be plaintext
        assertNotEquals(PASSWORD_VAL, passwordField.getValue());
        assertTrue(passwordField.isSensitive());

        // Verify that the value is decryptable (i.e., was encrypted correctly)
        assertEquals(PASSWORD_VAL, CryptoUtils.decrypt(passwordField.getValue()));
    }

    /**
     * Tests that the decrypt method only decrypts fields marked as sensitive.
     */
    @Test
    void decrypt_shouldDecryptSensitiveFieldsOnly() {
        Entry entry = createTestEntry();
        // Simulate an encrypted entry (as if loaded from DB)
        String encryptedPassword = CryptoUtils.encrypt(PASSWORD_VAL);
        entry.getField("Password").setValue(encryptedPassword);

        // Action
        Entry decryptedEntry = criptor.decrypt(entry);

        // Assert
        // Non-sensitive field was not touched
        assertEquals(USERNAME_VAL, decryptedEntry.getField("Username").getValue());
        // Sensitive field is now plaintext
        assertEquals(PASSWORD_VAL, decryptedEntry.getField("Password").getValue());
    }
}