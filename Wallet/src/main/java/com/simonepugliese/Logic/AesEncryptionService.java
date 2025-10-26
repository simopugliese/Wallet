package com.simonepugliese.Logic;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

public class AesEncryptionService implements EncryptionService {

    private static final int LUNGHEZZA_CHIAVE_AES = 256;
    private static final int LUNGHEZZA_TAG_GCM = 128;
    private static final int LUNGHEZZA_IV_GCM = 12;

    private static final String ALGORITMO_TRASFORMAZIONE = "AES/GCM/NoPadding";
    private static final String ALGORITMO_CHIAVE = "AES";

    @Override
    public String cifra(String testoInChiaro, byte[] chiave) throws Exception {
        if (chiave.length != 32) {
            throw new IllegalArgumentException("Chiave non valida: deve essere di 32 byte (256 bit).");
        }

        byte[] iv = new byte[LUNGHEZZA_IV_GCM];
        new SecureRandom().nextBytes(iv);

        SecretKey secretKey = new SecretKeySpec(chiave, ALGORITMO_CHIAVE);
        Cipher cipher = Cipher.getInstance(ALGORITMO_TRASFORMAZIONE);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(LUNGHEZZA_TAG_GCM, iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] testoCifrato = cipher.doFinal(testoInChiaro.getBytes("UTF-8"));

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + testoCifrato.length);
        byteBuffer.put(iv);
        byteBuffer.put(testoCifrato);
        byte[] ivPiùCifrato = byteBuffer.array();

        return Base64.getEncoder().encodeToString(ivPiùCifrato);
    }

    @Override
    public String decifra(String testoCifratoB64, byte[] chiave) throws Exception {
        if (chiave.length != 32) {
            throw new IllegalArgumentException("Chiave non valida: deve essere di 32 byte (256 bit).");
        }

        byte[] ivPiùCifrato = Base64.getDecoder().decode(testoCifratoB64);

        ByteBuffer byteBuffer = ByteBuffer.wrap(ivPiùCifrato);

        byte[] iv = new byte[LUNGHEZZA_IV_GCM];
        byteBuffer.get(iv);

        byte[] testoCifrato = new byte[byteBuffer.remaining()];
        byteBuffer.get(testoCifrato);

        SecretKey secretKey = new SecretKeySpec(chiave, ALGORITMO_CHIAVE);
        Cipher cipher = Cipher.getInstance(ALGORITMO_TRASFORMAZIONE);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(LUNGHEZZA_TAG_GCM, iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        byte[] testoInChiaroBytes = cipher.doFinal(testoCifrato);
        return new String(testoInChiaroBytes, "UTF-8");
    }
}
