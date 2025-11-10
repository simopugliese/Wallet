package com.simonepugliese.Saver;

import com.simonepugliese.Criptor.CryptoUtils;
import com.simonepugliese.Criptor.WifiItemCriptor;
import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.WifiItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WifiItemSaverTest {

    private WifiItemSaver saver;
    private WifiItemCriptor criptor;
    private static final String TABLE_NAME = "Wifi";
    private static final String MASTER_PASS = "chiave_master_segreta_per_i_test";


    @BeforeEach
    void setup() {
        // Inizializza CryptoUtils e DbConnector (Singleton)
        CryptoUtils.masterPassSet(MASTER_PASS);
        saver = new WifiItemSaver();
        criptor = new WifiItemCriptor();

        // Pulisce la tabella per garantire l'isolamento del test
        try (Connection conn = DbConnector.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            // Assicurati che la tabella esista prima di pulirla (DbConnector la crea, ma WifiItemSaverTest potrebbe girare prima)
            // Nota: WifiItemSaver non è nel DbConnector.java originale, assumo che tu l'abbia aggiunto.
            // Se DbConnector non crea la tabella Wifi, questo test fallirà.
            // Per sicurezza, aggiungo la CREATE TABLE IF NOT EXISTS qui.
            String createSql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + "description TEXT PRIMARY KEY, "
                    + "ssid TEXT, "
                    + "password TEXT)";
            stmt.execute(createSql);

            stmt.executeUpdate("DELETE FROM " + TABLE_NAME);
        } catch (SQLException e) {
            fail("Impossibile pulire la tabella per il test: " + e.getMessage());
        }
    }

    @Test
    void salva_shouldInsertNewItem() {
        WifiItem item = new WifiItem("Test-Insert", "SSID-1", "Pass-1");

        // Cripta l'oggetto prima di salvarlo
        WifiItem itemCriptato = (WifiItem) criptor.cripta(item);
        saver.salva(itemCriptato);

        List<Item> loadedItems = saver.carica();

        assertEquals(1, loadedItems.size());

        // L'oggetto caricato DEVE essere criptato
        WifiItem loadedItem = (WifiItem) loadedItems.get(0);
        assertNotEquals("SSID-1", loadedItem.getSSID());

        // Verifica che la decrittazione funzioni
        WifiItem decryptedItem = (WifiItem) criptor.decripta(loadedItem);
        assertEquals("SSID-1", decryptedItem.getSSID());
        assertEquals("Pass-1", decryptedItem.getPassword());
    }

    @Test
    void salva_shouldUpdateExistingItemOnSameDescription() {
        // 1. Inserisce l'item iniziale (criptato)
        WifiItem initialItem = new WifiItem("Update-Key", "Old-SSID", "Old-Pass");
        saver.salva(criptor.cripta(initialItem));

        // 2. Modifica l'item (stessa description) e cripta
        WifiItem updatedItem = new WifiItem("Update-Key", "New-SSID", "New-Pass");
        saver.salva(criptor.cripta(updatedItem));

        // 3. Carica e verifica che ci sia solo un record
        List<Item> loadedItems = saver.carica();
        assertEquals(1, loadedItems.size(), "Dovrebbe esserci un solo record (UPDATE).");

        // 4. Decripta e verifica i dati aggiornati
        WifiItem decryptedItem = (WifiItem) criptor.decripta(loadedItems.get(0));

        assertEquals("New-SSID", decryptedItem.getSSID());
        assertEquals("New-Pass", decryptedItem.getPassword());
    }

    @Test
    void carica_shouldReturnEmptyListIfNoItems() {
        List<Item> loadedItems = saver.carica();
        assertTrue(loadedItems.isEmpty());
    }
}