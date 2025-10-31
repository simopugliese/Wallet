package com.simonepugliese.Criptor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {

    // ⚠ DA SOSTITUIRE: Usare la password dell'utente o una chiave derivata in modo sicuro
    private static final String MASTER_PASS_PLACEHOLDER = "LaMiaChiaveSegretaPerWallet";

    private static final int GCM_IV_LENGTH = 12; // Initialization Vector (IV) length for GCM
    private static final int GCM_TAG_LENGTH = 16; // 128 bit Authentication Tag length
    private static final int SALT_LENGTH = 16; // Random Salt length for PBKDF2
    private static final int KEY_LENGTH = 256; // AES Key length in bits
    private static final int ITERATIONS = 65536; // PBKDF2 Iterations
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Deriva una chiave AES-256 da una passphrase e un salt utilizzando PBKDF2.
     */
    private static SecretKey getAESKey(byte[] saltBytes) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(MASTER_PASS_PLACEHOLDER.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKey secretKey = factory.generateSecret(spec);
            return new SecretKeySpec(secretKey.getEncoded(), "AES");
        } catch (Exception e) {
            throw new RuntimeException("Errore nella derivazione della chiave", e);
        }
    }

    /**
     * Cripta una stringa con AES-256 GCM. Il risultato Base64 contiene: Salt | IV | Ciphertext.
     */
    public static String encrypt(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        try {
            // Genera Salt (16 byte) e IV (12 byte) casuali
            byte[] salt = new byte[SALT_LENGTH];
            secureRandom.nextBytes(salt);
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            // Deriva la chiave e inizializza il Cipher
            SecretKey key = getAESKey(salt);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            // Cripta e concatena Salt, IV e Ciphertext
            byte[] cipherText = cipher.doFinal(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
            byteBuffer.put(salt);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());

        } catch (Exception e) {
            throw new RuntimeException("Crittografia fallita", e);
        }
    }

    /**
     * Decripta una stringa (AES-256 GCM) estraendo Salt, IV e Ciphertext.
     */
    public static String decrypt(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            return encryptedValue;
        }

        try {
            // Decodifica e separa i componenti (Salt, IV, Ciphertext)
            byte[] decoded = Base64.getDecoder().decode(encryptedValue);
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);

            byte[] salt = new byte[SALT_LENGTH];
            byteBuffer.get(salt);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] cipherTextWithTag = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherTextWithTag);

            // Deriva la chiave e inizializza il Cipher in modalità Decrypt
            SecretKey key = getAESKey(salt);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            // Decripta
            byte[] originalBytes = cipher.doFinal(cipherTextWithTag);

            return new String(originalBytes, java.nio.charset.StandardCharsets.UTF_8);

        } catch (Exception e) {
            // Eccezione critica: se il Tag GCM non corrisponde o la chiave è errata, l'integrità è compromessa.
            System.err.println("AVVISO: Decrittografia fallita. I dati potrebbero essere corrotti o la chiave errata.");
            throw new RuntimeException("Decrittografia fallita", e);
        }
    }
}