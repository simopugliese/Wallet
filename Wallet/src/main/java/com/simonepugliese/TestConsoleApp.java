package com.simonepugliese;

import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Model.Field;
import com.simonepugliese.Model.FieldType;
import com.simonepugliese.Persistence.DbConnector;
import com.simonepugliese.Security.CryptoUtils;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Classe main di esempio per testare il funzionamento del WalletManager.
 */
public class TestConsoleApp {

    public static void main(String[] args) {
        System.out.println("Avvio test app console...");

        try {
            //IMPOSTA DB nella cartella principale del progetto
            String dbPath = Paths.get("NOME.db").toAbsolutePath().toString();
            DbConnector.setJdbcUrl(dbPath);
            System.out.println("Database impostato su: " + dbPath);
            // Imposta la master password per la crittografia
            CryptoUtils.setMasterPassword("password-segreta-123");
            System.out.println("Master password impostata.");
            // Creiamo il manager tramite la factory
            WalletManager manager = WalletFactory.createWalletManager();
            System.out.println("WalletManager creato.");
            // Creiamo una entry "Google"
            Entry entryGoogle = new Entry("Account Google", Category.LOGIN);
            entryGoogle.putField("Username", new Field("mia.email@gmail.com", FieldType.TEXT, false)); // false = non sensibile
            entryGoogle.putField("Password", new Field("passwordSuperSegreta!", FieldType.PASSWORD, true)); // true = sensibile
            entryGoogle.putField("URL", new Field("https://accounts.google.com", FieldType.URL, false));
            // Salviamo (il manager la renderà criptata)
            manager.saveEntry(entryGoogle);
            System.out.println("Entry 'Google' salvata (ID: " + entryGoogle.getId() + ")");
            // Creiamo una entry "WiFi"
            Entry entryWifi = new Entry("WiFi Casa", Category.WIFI);
            entryWifi.putField("SSID", new Field("ReteCasaMia", FieldType.TEXT, false));
            entryWifi.putField("Password WiFi", new Field("pass-wifi-123", FieldType.PASSWORD, true)); // true = sensibile
            //Salviamo (il manager la renderà criptata)
            manager.saveEntry(entryWifi);
            System.out.println("Entry 'WiFi Casa' salvata (ID: " + entryWifi.getId() + ")");
            // --- READ ---
            System.out.println("\n--- Caricamento Riepiloghi (per la lista) ---");
            List<Entry> summaries = manager.loadAllEntrySummaries();
            System.out.println("Trovate " + summaries.size() + " voci:");
            for (Entry summary : summaries) {
                System.out.println("  - ID: " + summary.getId() + ", Desc: " + summary.getDescription() + ", Cat: " + summary.getCategory());
            }
            // --- LETTURA DETTAGLIO (READ + DECRYPT) ---
            String idDaCaricare = entryGoogle.getId();
            System.out.println("\n--- Caricamento Entry Completa (ID: " + idDaCaricare + ") ---");
            // Carichiamo l'entry completa. Il manager la decritterà automaticamente.
            Optional<Entry> loadedOpt = manager.loadAndDecryptEntry(idDaCaricare);
            if (loadedOpt.isPresent()) {
                Entry loaded = loadedOpt.get();
                System.out.println("Descrizione: " + loaded.getDescription());
                // I campi sono decifrati e pronti all'uso
                System.out.println("Username: " + loaded.getField("Username").getValue());
                System.out.println("Password (decrittografata): " + loaded.getField("Password").getValue());
            } else {
                System.out.println("ERRORE: Entry non trovata!");
            }
            // --- ELIMINAZIONE (DELETE) ---
            String idDaEliminare = entryWifi.getId();
            System.out.println("\n--- Eliminazione Entry (ID: " + idDaEliminare + ") ---");
            manager.deleteEntry(idDaEliminare);
            System.out.println("Entry WiFi eliminata.");
            // --- VERIFICA FINALE ---
            List<Entry> finalSummaries = manager.loadAllEntrySummaries();
            System.out.println("Voci rimanenti nel database: " + finalSummaries.size());
            for (Entry summary : finalSummaries) {
                System.out.println("  - " + summary.getDescription());
            }
        } catch (Exception e) {
            System.err.println("Si è verificato un errore critico:");
            e.printStackTrace();
        }
    }
}