package com.simonepugliese;

import com.simonepugliese.Criptor.CreditCardCriptor;
import com.simonepugliese.Criptor.CryptoUtils;
import com.simonepugliese.Criptor.LoginItemCriptor;
import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.LoginItem;
import com.simonepugliese.Manager.Manager;
import com.simonepugliese.Saver.CreditCardSaver;
import com.simonepugliese.Saver.DbConnector;
import com.simonepugliese.Saver.LoginItemSaver;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class WalletController {

    // Componenti UI per Login/Setup
    @FXML private VBox loginContainer;
    @FXML private PasswordField masterPasswordField;
    @FXML private Label messageLabel;
    @FXML private Button loginButton;
    @FXML private Label dbPathLabel;
    @FXML private Button selectDbButton;

    // Componenti UI per Dashboard
    @FXML private TabPane mainTabPane;
    @FXML private TableView<Item> itemTable;
    @FXML private TableColumn<Item, String> descriptionColumn;
    @FXML private TableColumn<Item, String> typeColumn;

    // Componenti UI per Form Salvataggio (Login)
    @FXML private TextField loginDescriptionField;
    @FXML private TextField loginUsernameField;
    @FXML private TextField loginPasswordField;
    @FXML private TextField loginUrlField;

    // Logica Applicativa
    private File walletDbFile;
    private Manager loginManager;
    private Manager creditCardManager;
    private final ObservableList<Item> walletItems = FXCollections.observableArrayList();

    // Logica di Sicurezza (Timeout)
    private static final Duration TIMEOUT_DURATION = Duration.seconds(300); // 5 minuti
    private PauseTransition securityTimeout;

    @FXML
    public void initialize() {
        // Nasconde la Dashboard e prepara la tabella
        mainTabPane.setVisible(false);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        itemTable.setItems(walletItems);

        setupTimeout();
    }

    // Configura il timer di sicurezza
    private void setupTimeout() {
        securityTimeout = new PauseTransition(TIMEOUT_DURATION);
        securityTimeout.setOnFinished(event -> lockApplication());
    }

    // Resetta il timer ad ogni interazione riuscita
    private void resetTimeout() {
        if (securityTimeout != null && mainTabPane.isVisible()) {
            securityTimeout.playFromStart();
        }
    }

    // Metodo per la selezione del file DB
    @FXML
    protected void onSelectDbButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona il tuo Wallet Database (.db)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SQLite Database", "*.db")
        );

        // Usa il componente come riferimento per la Stage
        Stage currentStage = (Stage) selectDbButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(currentStage);

        if (selectedFile != null) {
            walletDbFile = selectedFile;
            dbPathLabel.setText("DB Selezionato: " + walletDbFile.getName());

            // Aggiorna l'URL JDBC e forza l'inizializzazione del DB
            DbConnector.setJdbcUrl(walletDbFile.getAbsolutePath());
            messageLabel.setText("Inserisci la Master Password.");
        }
    }

    private void setupManagers() {
        // Inizializza i Manager con i Saver che useranno il path del DB dinamico
        loginManager = new Manager(new LoginItemCriptor(), new LoginItemSaver());
        creditCardManager = new Manager(new CreditCardCriptor(), new CreditCardSaver());
    }

    @FXML
    protected void onLoginButtonClick() {
        String masterPass = masterPasswordField.getText();

        if (walletDbFile == null || !walletDbFile.exists()) {
            messageLabel.setText("ERRORE: Devi prima selezionare il file Wallet DB.");
            return;
        }

        if (masterPass.isEmpty()) {
            messageLabel.setText("Inserisci la Master Password.");
            return;
        }

        // 1. Inizializza la chiave crittografica
        CryptoUtils.masterPassSet(masterPass);

        try {
            setupManagers();

            // 2. Verifica l'autenticità tentando di caricare/decriptare i dati
            List<Item> initialItems = loginManager.caricaPoiDecripta();
            initialItems.addAll(creditCardManager.caricaPoiDecripta());

            // Login riuscito: UNLOCK
            unlockApplication(initialItems);

        } catch (RuntimeException e) {
            // Se la decrittografia fallisce (Tag GCM errato o eccezione SQL)
            messageLabel.setText("Password errata o file DB corrotto.");
            messageLabel.setStyle("-fx-text-fill: red;");

            // 3. LOCK e pulisci la chiave in memoria
            lockApplication();
            masterPasswordField.clear();
        }
    }

    private void unlockApplication(List<Item> initialItems) {
        messageLabel.setText("Accesso Riuscito!");
        messageLabel.setStyle("-fx-text-fill: green;");

        walletItems.setAll(initialItems);

        // Nasconde i controlli di login/setup e mostra la Dashboard
        loginContainer.setVisible(false);
        mainTabPane.setVisible(true);

        // Avvia il timer di timeout
        resetTimeout();
    }

    // Metodo per il blocco (chiamato dal timeout o fallimento login)
    private void lockApplication() {
        // 1. Pulizia e blocco di sicurezza (Zeroing out)
        CryptoUtils.masterPassSet("");

        // 2. Aggiorna la GUI
        mainTabPane.setVisible(false);
        loginContainer.setVisible(true);
        // Visualizza il path del DB selezionato, ma richiede re-login
        dbPathLabel.setText("DB Selezionato: " + (walletDbFile != null ? walletDbFile.getName() : "Nessuno"));
        messageLabel.setText("Sessione scaduta o bloccata. Reinserisci la Master Password.");
        masterPasswordField.clear();
        walletItems.clear(); // Pulisci i dati decriptati dalla memoria
    }

    // Logica di salvataggio del LoginItem
    @FXML
    protected void onSaveLoginClick() {
        String description = loginDescriptionField.getText();
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();
        String url = loginUrlField.getText();

        if (loginManager == null) {
            messageLabel.setText("Errore di sessione: Manager non inizializzato.");
            return;
        }

        if (description.isEmpty() || username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Descrizione, Username e Password sono obbligatori.");
            return;
        }

        LoginItem newItem = new LoginItem(description, username, password, url);

        try {
            loginManager.criptaPoiSalva(newItem);

            // Ricarica tutti i dati (necessario per l'UPSERT)
            List<Item> updatedItems = loginManager.caricaPoiDecripta();
            updatedItems.addAll(creditCardManager.caricaPoiDecripta());

            walletItems.setAll(updatedItems);

            messageLabel.setText("Login salvato con successo: " + description);

            // Pulisci i campi del form dopo il salvataggio
            loginDescriptionField.clear();
            loginUsernameField.clear();
            loginPasswordField.clear();
            loginUrlField.clear();

            // SECURITY: Resetta il timer di timeout dopo un'attività riuscita
            resetTimeout();

        } catch (RuntimeException e) {
            messageLabel.setText("Errore durante il salvataggio nel database.");
            System.err.println("Errore durante il salvataggio nel DB: " + e.getMessage());
        }
    }

    // Metodo per aggiungere listener di interazione alla Scene (chiamato da WalletApplication)
    public void setupInteractionListener(Scene scene) {
        // Interazione del mouse
        scene.setOnMouseMoved(e -> resetTimeout());
        scene.setOnMousePressed(e -> resetTimeout());

        // Interazione da tastiera
        scene.setOnKeyPressed(e -> resetTimeout());
    }
}