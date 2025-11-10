package com.simonepugliese.Security;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario per la classe di utility {@link CryptoUtils}.
 * <p>
 * Verifica i seguenti scenari:
 * 1. Ciclo Encrypt/Decrypt: Un valore criptato può essere decriptato correttamente.
 * 2. Salt/IV Casuali: La crittografia produce ciphertext diversi per lo stesso input.
 * 3. Gestione Chiave: La decrittografia fallisce se la master password è errata.
 * 4. Casi Limite: Gestione corretta di input nulli o vuoti.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CryptoUtilsTest {

    private static final String MASTER_PASS = "chiave_segreta_per_i_test_123!";
    private static final String PLAINTEXT = "Questo è un segreto";

    /**
     * Imposta la master password una sola volta per tutti i test.
     */
    @BeforeAll
    static void setup() {
        CryptoUtils.setMasterPassword(MASTER_PASS);
    }

    @Test
    @Order(1)
    void encryptDecrypt_shouldReturnOriginalString() {
        String encrypted = CryptoUtils.encrypt(PLAINTEXT);

        // Verifica che il ciphertext non sia l'originale
        assertNotNull(encrypted);
        assertNotEquals(PLAINTEXT, encrypted);

        String decrypted = CryptoUtils.decrypt(encrypted);

        // Verifica che il ciclo si chiuda
        assertEquals(PLAINTEXT, decrypted);
    }

    @Test
    @Order(2)
    void encrypt_shouldProduceDifferentCiphertext() {
        // La crittografia GCM con Salt e IV casuali DEVE produrre
        // ciphertext diversi per lo stesso input.
        String encrypted1 = CryptoUtils.encrypt(PLAINTEXT);
        String encrypted2 = CryptoUtils.encrypt(PLAINTEXT);

        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    @Order(3)
    void decrypt_withWrongMasterPass_shouldThrowRuntimeException() {
        // 1. Cripta con la chiave corretta
        String encrypted = CryptoUtils.encrypt("Un altro segreto");

        // 2. Imposta una master password ERRATA
        CryptoUtils.setMasterPassword("CHIAVE-SBAGLIATA");

        // 3. Verifica che la decrittografia fallisca (AES GCM lancia eccezioni per tag non validi)
        assertThrows(RuntimeException.class, () -> {
            CryptoUtils.decrypt(encrypted);
        }, "La decrittografia con chiave errata dovrebbe lanciare un'eccezione");

        // 4. Ripristina la chiave corretta per altri test (se ce ne fossero)
        CryptoUtils.setMasterPassword(MASTER_PASS);
    }

    @Test
    @Order(4)
    void encryptDecrypt_shouldHandleNullAndEmpty() {
        // Testa i casi limite
        assertNull(CryptoUtils.encrypt(null));
        assertNull(CryptoUtils.decrypt(null));
        assertEquals("", CryptoUtils.encrypt(""));
        assertEquals("", CryptoUtils.decrypt(""));
    }
}