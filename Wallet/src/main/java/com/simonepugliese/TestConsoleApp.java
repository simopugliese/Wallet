package com.simonepugliese;

import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Model.Field;
import com.simonepugliese.Model.FieldType;
import com.simonepugliese.Persistence.DbConnector;
import com.simonepugliese.Security.CryptoUtils;

// Importa le classi di SLF4J
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Classe main di esempio per testare il funzionamento del WalletManager.
 */
public class TestConsoleApp {

    private static final Logger log = LoggerFactory.getLogger(TestConsoleApp.class);

    public static void main(String[] args) {
        log.info("Avvio test app console...");

        try {
            //IMPOSTA DB nella cartella principale del progetto
            String dbPath = Paths.get("wallet_main_test.db").toAbsolutePath().toString();
            DbConnector.setJdbcUrl(dbPath);
            // Imposta la master password per la crittografia
            CryptoUtils.setMasterPassword("password-segreta-123");
            // Creiamo il manager tramite la factory
            WalletManager manager = WalletFactory.createWalletManager();
            // Creiamo una entry "Google"
            Entry entryGoogle = new Entry("Account Google", Category.LOGIN);
            entryGoogle.putField("Username", new Field("mia.email@gmail.com", FieldType.TEXT, false)); // false = non sensibile
            entryGoogle.putField("Password", new Field("passwordSuperSegreta!", FieldType.PASSWORD, true)); // true = sensibile
            entryGoogle.putField("URL", new Field("https://accounts.google.com", FieldType.URL, false));
            // Salviamo
            manager.saveEntry(entryGoogle);
            log.info("Entry 'Google' creata (ID: {})", entryGoogle.getId());
            // Creiamo una entry "WiFi"
            Entry entryWifi = new Entry("WiFi Casa", Category.WIFI);
            entryWifi.putField("SSID", new Field("ReteCasaMia", FieldType.TEXT, false));
            entryWifi.putField("Password WiFi", new Field("pass-wifi-123", FieldType.PASSWORD, true)); // true = sensibile
            //Salviamo
            manager.saveEntry(entryWifi);
            log.info("Entry 'WiFi Casa' creata (ID: {})", entryWifi.getId());
            // --- READ ---
            log.info("\n--- Caricamento Riepiloghi (per la lista) ---");
            List<Entry> summaries = manager.loadAllEntrySummaries();
            log.info("Trovate {} voci:", summaries.size());
            for (Entry summary : summaries) {
                log.info("  - ID: {}, Desc: {}, Cat: {}", summary.getId(), summary.getDescription(), summary.getCategory());
            }
            // --- LETTURA DETTAGLIO (READ + DECRYPT) ---
            String idDaCaricare = entryGoogle.getId();
            log.info("\n--- Caricamento Entry Completa (ID: {}) ---", idDaCaricare);
            // Carichiamo l'entry completa.
            Optional<Entry> loadedOpt = manager.loadAndDecryptEntry(idDaCaricare);

            if (loadedOpt.isPresent()) {
                Entry loaded = loadedOpt.get();
                log.info("Descrizione: {}", loaded.getDescription());
                // I campi sono decifrati e pronti all'uso
                log.info("Username: {}", loaded.getField("Username").getValue());
                log.info("Password (decrittografata): {}", loaded.getField("Password").getValue());
            } else {
                log.warn("ERRORE: Entry non trovata!");
            }
            // --- ELIMINAZIONE (DELETE) ---
            String idDaEliminare = entryWifi.getId();
            log.info("\n--- Eliminazione Entry (ID: {}) ---", idDaEliminare);
            manager.deleteEntry(idDaEliminare);
            // --- VERIFICA FINALE ---
            log.info("\n--- Verifica Finale Riepiloghi ---");
            List<Entry> finalSummaries = manager.loadAllEntrySummaries();
            log.info("Voci rimanenti nel database: {}", finalSummaries.size());
            for (Entry summary : finalSummaries) {
                log.info("  - {}", summary.getDescription());
            }
            log.info("Test app console completato.");
        } catch (Exception e) {
            // Questo cattura qualsiasi errore fatale (es. setJdbcUrl dopo getInstance)
            log.error("Si è verificato un errore critico non gestito:", e);
        }
    }
}