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
 * Test unitario per {@link EntryCriptor}.
 * <p>
 * Verifica che la strategia di crittografia applichi correttamente
 * {@link CryptoUtils} solo ai campi contrassegnati come 'sensitive'.
 */
class EntryCriptorTest {

    private EntryCriptor criptor;
    private static final String MASTER_PASS = "test_pass_criptor";
    private static final String USERNAME_VAL = "user@example.com";
    private static final String PASSWORD_VAL = "P4$$w0rd_S3gret4!";

    @BeforeAll
    static void setupCrypto() {
        // Il Criptor dipende da CryptoUtils, che deve essere inizializzato
        CryptoUtils.setMasterPassword(MASTER_PASS);
    }

    @BeforeEach
    void setup() {
        this.criptor = new EntryCriptor();
    }

    /**
     * Costruisce una Entry di test con un campo sensibile e uno non.
     */
    private Entry createTestEntry() {
        Entry entry = new Entry("Test Login", Category.LOGIN);
        // Campo NON sensibile
        entry.putField("Username", new Field(USERNAME_VAL, FieldType.TEXT, false));
        // Campo SENSIBILE
        entry.putField("Password", new Field(PASSWORD_VAL, FieldType.PASSWORD, true));
        return entry;
    }

    @Test
    void encrypt_shouldEncryptSensitiveFieldsOnly() {
        Entry entry = createTestEntry();

        // Azione
        Entry encryptedEntry = criptor.encrypt(entry);

        // Assert
        Field usernameField = encryptedEntry.getField("Username");
        Field passwordField = encryptedEntry.getField("Password");

        // Il campo non sensibile DEVE rimanere in chiaro
        assertEquals(USERNAME_VAL, usernameField.getValue());
        assertFalse(usernameField.isSensitive());

        // Il campo sensibile NON DEVE essere in chiaro
        assertNotEquals(PASSWORD_VAL, passwordField.getValue());
        assertTrue(passwordField.isSensitive());

        // Verifica che il valore sia decrittabile (cioè è stato criptato correttamente)
        assertEquals(PASSWORD_VAL, CryptoUtils.decrypt(passwordField.getValue()));
    }

    @Test
    void decrypt_shouldDecryptSensitiveFieldsOnly() {
        Entry entry = createTestEntry();
        // Simula una entry criptata (come se fosse letta dal DB)
        String encryptedPassword = CryptoUtils.encrypt(PASSWORD_VAL);
        entry.getField("Password").setValue(encryptedPassword);

        // Azione
        Entry decryptedEntry = criptor.decrypt(entry);

        // Assert
        // Il campo non sensibile non è stato toccato
        assertEquals(USERNAME_VAL, decryptedEntry.getField("Username").getValue());
        // Il campo sensibile è ora in chiaro
        assertEquals(PASSWORD_VAL, decryptedEntry.getField("Password").getValue());
    }
}