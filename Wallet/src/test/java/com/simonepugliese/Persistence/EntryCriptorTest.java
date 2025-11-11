package com.simonepugliese.Persistence;

import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Model.Field;
import com.simonepugliese.Model.FieldType;
import com.simonepugliese.Security.CryptoUtils; // Importato per il test
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per EntryCriptor.
 * Verifica che la logica di crittografia sia applicata *solo* ai campi sensibili.
 */
class EntryCriptorTest {

    private static final String MASTER_PASS_STR = "test_pass_criptor";
    private static final char[] MASTER_PASS_CHARS = MASTER_PASS_STR.toCharArray();
    private static final String USERNAME_VAL = "user@example.com";
    private static final String PASSWORD_VAL = "P4$$w0rd_S3gret4!";

    private EntryCriptor criptor;

    @BeforeEach
    void setup() {
        // Creiamo un nuovo criptor per ogni test, con la sua password
        this.criptor = new EntryCriptor(MASTER_PASS_STR);
    }

    /**
     * Crea un Entry di test con un campo sensibile e uno no.
     */
    private Entry createTestEntry() {
        Entry entry = new Entry("Test Login", Category.LOGIN);
        // Non-sensibile
        entry.putField("Username", new Field(USERNAME_VAL, FieldType.TEXT, false));
        // Sensibile
        entry.putField("Password", new Field(PASSWORD_VAL, FieldType.PASSWORD, true));
        return entry;
    }

    /**
     * SCENARIO 1 (Encrypt): Verifica che encrypt() modifichi
     * solo il campo sensibile.
     */
    @Test
    void encrypt_shouldEncryptSensitiveFieldsOnly() {
        Entry entry = createTestEntry();

        // Azione
        Entry encryptedEntry = criptor.encrypt(entry);

        // Assert
        Field usernameField = encryptedEntry.getField("Username");
        Field passwordField = encryptedEntry.getField("Password");

        // Il campo NON-sensibile DEVE rimanere in chiaro
        assertEquals(USERNAME_VAL, usernameField.getValue(), "Campo non sensibile non deve cambiare");
        assertFalse(usernameField.isSensitive());

        // Il campo sensibile NON DEVE essere in chiaro
        assertTrue(passwordField.isSensitive());
        assertNotNull(passwordField.getValue());
        assertNotEquals(PASSWORD_VAL, passwordField.getValue(), "Campo sensibile deve essere cifrato");

        // Test "cattivo": verifico che sia *effettivamente* decifrabile
        // con la password giusta, altrimenti l'encrypt è rotto.
        String decryptedPass = CryptoUtils.decrypt(passwordField.getValue(), MASTER_PASS_CHARS);
        assertEquals(PASSWORD_VAL, decryptedPass, "Il valore cifrato deve essere decifrabile");
    }

    /**
     * SCENARIO 2 (Decrypt): Verifica che decrypt() modifichi
     * solo il campo sensibile (che arriva cifrato).
     */
    @Test
    void decrypt_shouldDecryptSensitiveFieldsOnly() {
        Entry entry = createTestEntry();

        // Setup: simuliamo un entry come se fosse letta dal DB (con pass cifrata)
        String encryptedPassword = CryptoUtils.encrypt(PASSWORD_VAL, MASTER_PASS_CHARS);
        entry.getField("Password").setValue(encryptedPassword);

        // Verifichiamo lo stato iniziale (pass è cifrata)
        assertNotEquals(PASSWORD_VAL, entry.getField("Password").getValue());

        // Azione
        Entry decryptedEntry = criptor.decrypt(entry);

        // Assert
        // Il campo NON-sensibile è rimasto intatto
        assertEquals(USERNAME_VAL, decryptedEntry.getField("Username").getValue());

        // Il campo sensibile ora è in chiaro
        assertEquals(PASSWORD_VAL, decryptedEntry.getField("Password").getValue(), "Password deve essere decifrata");
    }
}