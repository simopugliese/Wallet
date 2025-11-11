package com.simonepugliese.Security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per CryptoUtils focalizzato sugli scenari di sicurezza.
 * Testiamo il ciclo encrypt/decrypt, i casi limite e i fallimenti.
 */
class CryptoUtilsTest {

    private final char[] MASTER_PASS = "chiave_segreta_per_i_test_123!".toCharArray();
    private final char[] WRONG_PASS = "CHIAVE-SBAGLIATA-999".toCharArray();
    private final String PLAINTEXT = "Questo è un segreto 12345";

    /**
     * SCENARIO 1 (Happy Path): Verifica il ciclo completo encrypt -> decrypt.
     */
    @Test
    void encryptDecrypt_shouldReturnOriginalString_withCorrectPassword() {
        // Azione
        String encrypted = CryptoUtils.encrypt(PLAINTEXT, MASTER_PASS);

        // Assert
        assertNotNull(encrypted);
        assertNotEquals(PLAINTEXT, encrypted, "Il testo cifrato non deve essere uguale al plaintext");

        // Azione
        String decrypted = CryptoUtils.decrypt(encrypted, MASTER_PASS);

        // Assert
        assertEquals(PLAINTEXT, decrypted, "Il testo decifrato deve corrispondere all'originale");
    }

    /**
     * SCENARIO 2 (Failure): Verifica che il decrypt fallisca con una password errata.
     * Questo è un test "cattivo" fondamentale.
     */
    @Test
    void decrypt_shouldThrowException_withWrongPassword() {
        // Setup
        String encrypted = CryptoUtils.encrypt(PLAINTEXT, MASTER_PASS);

        // Azione e Assert
        // Verifica che il decrypt lanci *specificamente* la nostra eccezione
        // (o una RuntimeException generica se la prima fallisce)
        assertThrows(DecryptionFailedException.class, () -> CryptoUtils.decrypt(encrypted, WRONG_PASS), "Il decrypt con password errata deve lanciare un'eccezione");
    }

    /**
     * SCENARIO 3 (Edge Cases): Verifica la gestione di null e stringhe vuote.
     */
    @Test
    void encryptDecrypt_shouldHandleNullAndEmptyStrings() {
        assertNull(CryptoUtils.encrypt(null, MASTER_PASS), "Encrypt di null deve restituire null");
        assertEquals("", CryptoUtils.encrypt("", MASTER_PASS), "Encrypt di stringa vuota deve restituire stringa vuota");

        assertNull(CryptoUtils.decrypt(null, MASTER_PASS), "Decrypt di null deve restituire null");
        assertEquals("", CryptoUtils.decrypt("", MASTER_PASS), "Decrypt di stringa vuota deve restituire stringa vuota");
    }

    /**
     * SCENARIO 4 (Uniqueness): Verifica che due encrypt dello stesso testo
     * producano risultati DIVERSI (grazie al sale random).
     */
    @Test
    void encrypt_shouldProduceDifferentCiphertext_forSameInput() {
        String encrypted1 = CryptoUtils.encrypt(PLAINTEXT, MASTER_PASS);
        String encrypted2 = CryptoUtils.encrypt(PLAINTEXT, MASTER_PASS);

        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        assertNotEquals(encrypted1, encrypted2, "Due encrypt uguali devono produrre ciphertext diversi (sale)");
    }
}