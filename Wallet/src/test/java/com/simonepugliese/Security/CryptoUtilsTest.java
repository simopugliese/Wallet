package com.simonepugliese.Security;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the {@link CryptoUtils} utility class.
 *
 * <p>This test validates the following scenarios:
 * <ol>
 * <li>Encrypt/Decrypt Cycle: A value can be encrypted and then decrypted back to the original.</li>
 * <li>Random Salt/IV: Encryption produces different ciphertexts for the same input.</li>
 * <li>Key Management: Decryption fails if the master password is incorrect.</li>
 * <li>Edge Cases: Correct handling of null or empty inputs.</li>
 * </ol>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CryptoUtilsTest {

    private static final String MASTER_PASS = "chiave_segreta_per_i_test_123!";
    private static final String PLAINTEXT = "Questo è un segreto";

    /**
     * Sets the master password a single time for all tests in this class.
     */
    @BeforeAll
    static void setup() {
        CryptoUtils.setMasterPassword(MASTER_PASS);
    }

    /**
     * Tests the standard encrypt-then-decrypt cycle.
     */
    @Test
    @Order(1)
    void encryptDecrypt_shouldReturnOriginalString() {
        String encrypted = CryptoUtils.encrypt(PLAINTEXT);

        // Verify that the ciphertext is not the original text
        assertNotNull(encrypted);
        assertNotEquals(PLAINTEXT, encrypted);

        String decrypted = CryptoUtils.decrypt(encrypted);

        // Verify that the cycle completes successfully
        assertEquals(PLAINTEXT, decrypted);
    }

    /**
     * Tests that two encryptions of the same plaintext produce different results,
     * as expected from using random Salts and IVs.
     */
    @Test
    @Order(2)
    void encrypt_shouldProduceDifferentCiphertext() {
        // GCM encryption with random Salt and IV MUST produce
        // different ciphertexts for the same input.
        String encrypted1 = CryptoUtils.encrypt(PLAINTEXT);
        String encrypted2 = CryptoUtils.encrypt(PLAINTEXT);

        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        assertNotEquals(encrypted1, encrypted2);
    }

    /**
     * Tests that decryption throws a RuntimeException if the master password
     * is incorrect (due to GCM tag validation failure).
     */
    @Test
    @Order(3)
    void decrypt_withWrongMasterPass_shouldThrowRuntimeException() {
        // 1. Encrypt with the correct key
        String encrypted = CryptoUtils.encrypt("Un altro segreto");

        // 2. Set an INCORRECT master password
        CryptoUtils.setMasterPassword("CHIAVE-SBAGLIATA");

        // 3. Verify decryption fails (AES GCM throws exceptions for invalid tags)
        assertThrows(RuntimeException.class, () -> CryptoUtils.decrypt(encrypted), "Decryption with the wrong key should throw an exception");

        // 4. Restore the correct key for any subsequent tests
        CryptoUtils.setMasterPassword(MASTER_PASS);
    }

    /**
     * Tests the handling of edge cases like null and empty strings.
     */
    @Test
    @Order(4)
    void encryptDecrypt_shouldHandleNullAndEmpty() {
        // Test edge cases
        assertNull(CryptoUtils.encrypt(null));
        assertNull(CryptoUtils.decrypt(null));
        assertEquals("", CryptoUtils.encrypt(""));
        assertEquals("", CryptoUtils.decrypt(""));
    }
}