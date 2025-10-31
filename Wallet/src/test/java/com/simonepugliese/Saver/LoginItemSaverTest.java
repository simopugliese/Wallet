package com.simonepugliese.Saver;

import com.simonepugliese.Criptor.CryptoUtils;
import com.simonepugliese.Criptor.LoginItemCriptor; // Import richiesto
import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.LoginItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoginItemSaverTest {

    private LoginItemSaver saver;
    private LoginItemCriptor criptor; // Aggiunto il Criptor per gestire i dati
    private static final String TABLE_NAME = "LoginsBasic";
    private static final String MASTER_PASS = "chiave_master_segreta_per_i_test";


    @BeforeEach
    void setup() {
        // Inizializza CryptoUtils e DbConnector (Singleton)
        CryptoUtils.masterPassSet(MASTER_PASS);
        saver = new LoginItemSaver();
        criptor = new LoginItemCriptor(); // Inizializza il Criptor

        // Pulisce la tabella per garantire l'isolamento del test
        try (Connection conn = DbConnector.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM " + TABLE_NAME);
        } catch (SQLException e) {
            fail("Impossibile pulire la tabella per il test: " + e.getMessage());
        }
    }

    @Test
    void salva_shouldInsertNewItem() {
        LoginItem item = new LoginItem("Test-Insert", "user1", "pass1", "url1.com");

        // FIX: Cripta l'oggetto prima di salvarlo nel DB
        LoginItem itemCriptato = (LoginItem) criptor.cripta(item);
        saver.salva(itemCriptato);

        List<Item> loadedItems = saver.carica();

        assertEquals(1, loadedItems.size());

        // L'oggetto caricato DEVE essere criptato
        LoginItem loadedItem = (LoginItem) loadedItems.get(0);

        // L'assertion ora funziona perché l'username nel DB è criptato
        assertNotEquals("user1", loadedItem.getUsername());

        // Verifica che la decrittazione funzioni (verificando il ciclo completo)
        LoginItem decryptedItem = (LoginItem) criptor.decripta(loadedItem);
        assertEquals("user1", decryptedItem.getUsername());
    }

    @Test
    void salva_shouldUpdateExistingItemOnSameDescription() {
        // 1. Inserisce l'item iniziale (criptato prima di salvare)
        LoginItem initialItem = new LoginItem("Update-Key", "olduser", "oldpass", "oldurl.com");
        saver.salva(criptor.cripta(initialItem));

        // 2. Modifica l'item (stessa description)
        LoginItem updatedItem = new LoginItem("Update-Key", "newuser", "newpass", "newurl.com");
        // FIX: Cripta l'item aggiornato prima di chiamare l'UPSERT
        saver.salva(criptor.cripta(updatedItem));

        // 3. Carica e verifica che ci sia solo un record
        List<Item> loadedItems = saver.carica();

        assertEquals(1, loadedItems.size(), "Dovrebbe esserci un solo record (UPDATE) e non due (INSERT).");

        // 4. Decripta e verifica i dati aggiornati
        LoginItem loadedItem = (LoginItem) loadedItems.get(0);

        // FIX: La decrittografia ora avviene su dati validi
        LoginItem decryptedItem = (LoginItem) criptor.decripta(loadedItem);

        assertEquals("newuser", decryptedItem.getUsername());
        assertEquals("newpass", decryptedItem.getPassword());
    }

    @Test
    void carica_shouldReturnEmptyListIfNoItems() {
        List<Item> loadedItems = saver.carica();
        assertTrue(loadedItems.isEmpty());
    }
}