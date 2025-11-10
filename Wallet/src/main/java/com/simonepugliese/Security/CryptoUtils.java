package com.simonepugliese.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for handling AES-256 GCM encryption and decryption.
 * <p>
 * This class provides static methods to encrypt and decrypt strings based
 * on a master password set via {@link #setMasterPassword(String)}.
 * It is final and cannot be instantiated.
 */
public final class CryptoUtils {

    // --- Private constants ---
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int PBKDF2_ITERATIONS = 65536;
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";

    /**
     * The master password used to derive the encryption key.
     * This is intentionally package-private to discourage access
     * from outside the security context.
     */
    private static String MASTER_PASSWORD = "";

    /**
     * A cryptographically secure random number generator.
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CryptoUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Sets the master password used for all encryption/decryption operations.
     * This MUST be called by the application's entry point before
     * any crypto operations are performed.
     *
     * @param password The user's master password.
     */
    public static void setMasterPassword(String password) {
        MASTER_PASSWORD = password;
        // Attempt to nullify the reference (basic security measure)
        password = null;
    }

    /**
     * Derives a 256-bit AES key from the master password and a salt
     * using PBKDF2.
     *
     * @param saltBytes The random salt (16 bytes).
     * @return A SecretKey for use with AES.
     */
    private static SecretKey getAESKey(byte[] saltBytes) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(
                    MASTER_PASSWORD.toCharArray(),
                    saltBytes,
                    PBKDF2_ITERATIONS,
                    KEY_LENGTH_BITS
            );
            SecretKey secretKey = factory.generateSecret(spec);
            return new SecretKeySpec(secretKey.getEncoded(), "AES");
        } catch (Exception e) {
            e.printStackTrace(); //TODO: Implement robust logging
            throw new RuntimeException("Error deriving AES key", e);
        }
    }

    /**
     * Creates and initializes a Cipher for a specific mode, salt, and IV.
     * This helper method encapsulates the duplicated logic from encrypt() and decrypt().
     *
     * @param mode The cipher mode (Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE).
     * @param salt The 16-byte salt.
     * @param iv   The 12-byte IV.
     * @return An initialized Cipher.
     * @throws Exception if cipher initialization fails.
     */
    private static Cipher createCipher(int mode, byte[] salt, byte[] iv) throws Exception {
        SecretKey key = getAESKey(salt);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(mode, key, gcmSpec);
        return cipher;
    }

    /**
     * Encrypts a plaintext string using AES-256 GCM.
     * The output is a Base64 string containing: [Salt | IV | Ciphertext].
     *
     * @param plaintext The string to encrypt.
     * @return The Base64 encoded ciphertext, or the original value if null/empty.
     */
    public static String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }

        try {
            byte[] salt = new byte[SALT_LENGTH];
            SECURE_RANDOM.nextBytes(salt);
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = createCipher(Cipher.ENCRYPT_MODE, salt, iv);

            byte[] cipherText = cipher.doFinal(plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            EncryptedPayload payload = new EncryptedPayload(salt, iv, cipherText);
            return payload.toBase64();

        } catch (Exception e) {
            e.printStackTrace(); //TODO: Implement robust logging
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypts a Base64 encoded string (Salt | IV | Ciphertext).
     *
     * @param encryptedValue The Base64 string to decrypt.
     * @return The original plaintext, or the original value if null/empty.
     */
    public static String decrypt(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            return encryptedValue;
        }

        try {
            EncryptedPayload payload = EncryptedPayload.fromBase64(encryptedValue);

            Cipher cipher = createCipher(Cipher.DECRYPT_MODE, payload.salt, payload.iv);

            byte[] originalBytes = cipher.doFinal(payload.cipherText);

            return new String(originalBytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace(); //TODO: Implement robust logging
            System.err.println("WARNING: Decryption failed. Data may be corrupt or key is incorrect.");
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * A private helper record to encapsulate the structure of the encrypted payload.
     * This record handles the serialization and deserialization of the
     * [Salt | IV | Ciphertext] byte array, removing duplicate logic
     * from encrypt() and decrypt().
     */
    private record EncryptedPayload(byte[] salt, byte[] iv, byte[] cipherText) {

        /**
         * Serializes the payload components into a single Base64 string.
         *
         * @return A Base64 string representing [Salt | IV | Ciphertext].
         */
        public String toBase64() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
            byteBuffer.put(salt);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        }

        /**
         * Deserializes a Base64 string into its payload components.
         *
         * @param base64 The Base64 string [Salt | IV | Ciphertext].
         * @return A new {@link EncryptedPayload} record.
         */
        public static EncryptedPayload fromBase64(String base64) {
            byte[] decoded = Base64.getDecoder().decode(base64);
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);

            byte[] salt = new byte[SALT_LENGTH];
            byteBuffer.get(salt);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            return new EncryptedPayload(salt, iv, cipherText);
        }
    }
}