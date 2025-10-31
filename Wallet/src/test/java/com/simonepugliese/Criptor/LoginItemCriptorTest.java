package com.simonepugliese.Criptor;

import com.simonepugliese.Item.LoginItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginItemCriptorTest {

    private static final String MASTER_PASS = "chiave_master_segreta_per_i_test";

    @BeforeAll
    static void setup() {
        // Garantisce che CryptoUtils abbia la chiave impostata
        CryptoUtils.masterPassSet(MASTER_PASS);
    }

    @Test
    void cripta_shouldEncryptSensitiveFields() {
        // Dato un LoginItem non criptato
        LoginItem originalItem = new LoginItem("Facebook", "simopugliese", "P4$$w0rd", "https://facebook.com");
        LoginItemCriptor criptor = new LoginItemCriptor();

        // Quando cripta
        LoginItem encryptedItem = (LoginItem) criptor.cripta(originalItem);

        // Allora i campi sensibili devono essere cambiati (crittografati)
        assertNotEquals("simopugliese", encryptedItem.getUsername());
        assertNotEquals("P4$$w0rd", encryptedItem.getPassword());

        // E la description deve rimanere la stessa
        assertEquals("Facebook", encryptedItem.getDescription());
    }

    @Test
    void decripta_shouldReturnOriginalValues() {
        // 1. Simula i dati criptati come se fossero caricati dal DB
        String fakeEncUsername = CryptoUtils.encrypt("simopugliese");
        String fakeEncPassword = CryptoUtils.encrypt("P4$$w0rd");
        String fakeEncUrlSito = CryptoUtils.encrypt("https://facebook.com");

        LoginItem encryptedItem = new LoginItem("Facebook", fakeEncUsername, fakeEncPassword, fakeEncUrlSito);
        LoginItemCriptor criptor = new LoginItemCriptor();

        // Quando decripta
        LoginItem decryptedItem = (LoginItem) criptor.decripta(encryptedItem);

        // Allora i campi devono tornare ai valori originali
        assertEquals("simopugliese", decryptedItem.getUsername());
        assertEquals("P4$$w0rd", decryptedItem.getPassword());
        assertEquals("https://facebook.com", decryptedItem.getUrlSito());
    }
}