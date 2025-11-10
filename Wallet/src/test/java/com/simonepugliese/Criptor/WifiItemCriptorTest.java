package com.simonepugliese.Criptor;

import com.simonepugliese.Item.WifiItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WifiItemCriptorTest {

    private static final String MASTER_PASS = "chiave_master_segreta_per_i_test";

    @BeforeAll
    static void setup() {
        // Garantisce che CryptoUtils abbia la chiave impostata
        CryptoUtils.masterPassSet(MASTER_PASS);
    }

    @Test
    void cripta_shouldEncryptSensitiveFields() {
        // Dato un WifiItem non criptato
        WifiItem originalItem = new WifiItem("Casa", "FASTWEB-123", "P4$$w0rdWifi");
        WifiItemCriptor criptor = new WifiItemCriptor();

        // Quando cripta
        WifiItem encryptedItem = (WifiItem) criptor.cripta(originalItem);

        // Allora i campi sensibili devono essere cambiati (crittografati)
        assertNotEquals("FASTWEB-123", encryptedItem.getSSID());
        assertNotEquals("P4$$w0rdWifi", encryptedItem.getPassword());

        // E la description deve rimanere la stessa
        assertEquals("Casa", encryptedItem.getDescription());
    }

    @Test
    void decripta_shouldReturnOriginalValues() {
        // 1. Simula i dati criptati come se fossero caricati dal DB
        String fakeEncSSID = CryptoUtils.encrypt("FASTWEB-123");
        String fakeEncPassword = CryptoUtils.encrypt("P4$$w0rdWifi");

        WifiItem encryptedItem = new WifiItem("Casa", fakeEncSSID, fakeEncPassword);
        WifiItemCriptor criptor = new WifiItemCriptor();

        // Quando decripta
        WifiItem decryptedItem = (WifiItem) criptor.decripta(encryptedItem);

        // Allora i campi devono tornare ai valori originali
        assertEquals("FASTWEB-123", decryptedItem.getSSID());
        assertEquals("P4$$w0rdWifi", decryptedItem.getPassword());
    }
}