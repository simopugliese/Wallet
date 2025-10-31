package com.simonepugliese.Criptor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {

    // Utilizza una chiave di test prevedibile
    private static final String MASTER_PASS = "chiave_master_segreta_per_i_test";
    private static final String TEST_STRING = "password12345";

    @BeforeAll
    static void setup() {
        // Imposta la Master Password per l'esecuzione dei test
        CryptoUtils.masterPassSet(MASTER_PASS);
    }

    @Test
    void encrypt_decrypt_shouldReturnOriginalString() {
        // 1. Cripta
        String encryptedValue = CryptoUtils.encrypt(TEST_STRING);

        // Verifica che la stringa criptata non sia vuota o uguale all'originale
        assertNotNull(encryptedValue);
        assertNotEquals(TEST_STRING, encryptedValue);

        // 2. Decripta
        String decryptedValue = CryptoUtils.decrypt(encryptedValue);

        // Verifica che la stringa decriptata sia uguale all'originale
        assertEquals(TEST_STRING, decryptedValue);
    }

    @Test
    void encrypt_shouldGenerateDifferentCiphertextForEachCall() {
        String encryptedValue1 = CryptoUtils.encrypt(TEST_STRING);
        String encryptedValue2 = CryptoUtils.encrypt(TEST_STRING);

        // Poiché GCM usa Salt e IV casuali, due chiamate con lo stesso input
        // devono produrre due output (ciphertext) diversi.
        assertNotEquals(encryptedValue1, encryptedValue2);

        // Verifica che entrambi siano comunque decriptabili
        assertEquals(TEST_STRING, CryptoUtils.decrypt(encryptedValue1));
        assertEquals(TEST_STRING, CryptoUtils.decrypt(encryptedValue2));
    }

    @Test
    void decrypt_withWrongMasterPass_shouldThrowRuntimeException() {
        // 1. Cripta con la chiave corretta
        String encryptedValue = CryptoUtils.encrypt(TEST_STRING);

        // 2. Imposta una chiave errata (simula un utente che sbaglia password)
        CryptoUtils.masterPassSet("chiave_sbagliata");

        // 3. Tenta la decrittazione (deve fallire a causa del Tag GCM errato)
        assertThrows(RuntimeException.class, () -> CryptoUtils.decrypt(encryptedValue));

        // Ripristina la chiave corretta per gli altri test
        CryptoUtils.masterPassSet(MASTER_PASS);
    }
}