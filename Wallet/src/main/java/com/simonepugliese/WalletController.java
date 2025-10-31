package com.simonepugliese;

import com.simonepugliese.Criptor.CreditCardCriptor;
import com.simonepugliese.Criptor.CryptoUtils;
import com.simonepugliese.Criptor.LoginItemCriptor;
import com.simonepugliese.Item.CreditCardItem;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
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

    // Componenti UI per Form Salvataggio (Carta di Credito)
    @FXML private TextField cardDescriptionField;
    @FXML private TextField cardOwnerField;
    @FXML private TextField cardBankField;
    @FXML private TextField cardNumberField;
    @FXML private PasswordField cardCvvField;
    @FXML private TextField cardExpirationField;

    // Componenti UI per Dettagli
    @FXML private Label detailDescriptionLabel;
    @FXML private Label detailTypeLabel;
    @FXML private Label detailMessageLabel;

    // Dettagli LoginItem
    @FXML private Label detailUsernameTitle;
    @FXML private Label detailUsernameLabel;
    @FXML private Label detailPasswordTitle;
    @FXML private Label detailPasswordLabel;
    @FXML private Label detailUrlTitle;
    @FXML private Label detailUrlLabel;

    // Dettagli CreditCardItem
    @FXML private Label detailOwnerTitle;
    @FXML private Label detailOwnerLabel;
    @FXML private Label detailBankTitle;
    @FXML private Label detailBankLabel;
    @FXML private Label detailNumberTitle;
    @FXML private Label detailNumberLabel;
    @FXML private Label detailCvvTitle;
    @FXML private Label detailCvvLabel;
    @FXML private Label detailExpirationTitle;
    @FXML private Label detailExpirationLabel;

    private final LoginItemCriptor loginCriptor = new LoginItemCriptor();
    private final CreditCardCriptor creditCardCriptor = new CreditCardCriptor();

    private File walletDbFile;
    private Manager loginManager;
    private Manager creditCardManager;
    private final ObservableList<Item> walletItems = FXCollections.observableArrayList();

    private static final Duration TIMEOUT_DURATION = Duration.seconds(300); // 5 minuti
    private PauseTransition securityTimeout;

    @FXML
    public void initialize() {
        mainTabPane.setVisible(false);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        itemTable.setItems(walletItems);

        setupTimeout();
        clearDetails();

        itemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDetails(newSelection);
            } else {
                clearDetails();
            }
        });
    }

    // -- GESTIONE SICUREZZA E STATO --

    private void setupTimeout() {
        securityTimeout = new PauseTransition(TIMEOUT_DURATION);
        securityTimeout.setOnFinished(event -> lockApplication());
    }

    private void resetTimeout() {
        if (securityTimeout != null && mainTabPane.isVisible()) {
            securityTimeout.playFromStart();
        }
    }

    private void setupManagers() {
        loginManager = new Manager(new LoginItemCriptor(), new LoginItemSaver());
        creditCardManager = new Manager(new CreditCardCriptor(), new CreditCardSaver());
    }

    private void unlockApplication(List<Item> initialItems) {
        messageLabel.setText("Accesso Riuscito!");
        messageLabel.setStyle("-fx-text-fill: green;");

        walletItems.setAll(initialItems);

        loginContainer.setVisible(false);
        mainTabPane.setVisible(true);

        resetTimeout();
    }

    // Metodo per il blocco (RISOLVE IndexOutOfBoundsException)
    private void lockApplication() {
        // 1. Pulizia e blocco di sicurezza (Zeroing out)
        CryptoUtils.masterPassSet("");

        // 2. Utilizza Platform.runLater per eseguire la pulizia GUI in modo sicuro
        // DOPO che JavaFX ha finito di elaborare tutti gli eventi di selezione e notifica.
        javafx.application.Platform.runLater(() -> {
            // Pulizia GUI (necessaria in caso di lock forzato)
            mainTabPane.setVisible(false);
            loginContainer.setVisible(true);

            itemTable.getSelectionModel().clearSelection();
            walletItems.clear();
            clearDetails();

            // Messaggi utente
            dbPathLabel.setText("DB Selezionato: " + (walletDbFile != null ? walletDbFile.getName() : "Nessuno"));
            messageLabel.setText("Sessione scaduta o bloccata. Reinserisci la Master Password.");
            masterPasswordField.clear();
        });
    }

    public void setupInteractionListener(Scene scene) {
        scene.setOnMouseMoved(e -> resetTimeout());
        scene.setOnMousePressed(e -> resetTimeout());
        scene.setOnKeyPressed(e -> resetTimeout());
    }

    // -- AZIONI UI --

    @FXML
    protected void onSelectDbButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona il tuo Wallet Database (.db)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SQLite Database", "*.db")
        );

        Stage currentStage = (Stage) selectDbButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(currentStage);

        if (selectedFile != null) {
            walletDbFile = selectedFile;
            dbPathLabel.setText("DB Selezionato: " + walletDbFile.getName());

            DbConnector.setJdbcUrl(walletDbFile.getAbsolutePath());
            messageLabel.setText("Inserisci la Master Password.");
        }
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

        CryptoUtils.masterPassSet(masterPass);

        try {
            setupManagers();

            List<Item> initialItems = loginManager.caricaPoiDecripta();
            initialItems.addAll(creditCardManager.caricaPoiDecripta());

            unlockApplication(initialItems);

        } catch (RuntimeException e) {
            messageLabel.setText("Password errata o file DB corrotto.");
            messageLabel.setStyle("-fx-text-fill: red;");
            lockApplication();
        }
    }

    // -- GESTIONE DATI --

    private void handleSaveAndReload() {
        try {
            List<Item> updatedItems = loginManager.caricaPoiDecripta();
            updatedItems.addAll(creditCardManager.caricaPoiDecripta());

            walletItems.setAll(updatedItems);
            resetTimeout();

        } catch (RuntimeException e) {
            messageLabel.setText("ERRORE: I dati sono stati corrotti durante l'aggiornamento. Blocco forzato.");
            System.err.println("Errore durante il ricaricamento: " + e.getMessage());
            lockApplication();
        }
    }

    @FXML
    protected void onSaveLoginClick() {
        String description = loginDescriptionField.getText();
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();
        String url = loginUrlField.getText();

        if (loginManager == null || description.isEmpty() || username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Descrizione, Username e Password sono obbligatori.");
            return;
        }

        LoginItem newItem = new LoginItem(description, username, password, url);
        try {
            loginManager.criptaPoiSalva(newItem);
            handleSaveAndReload();

            messageLabel.setText("Login salvato con successo: " + description);
            loginDescriptionField.clear(); loginUsernameField.clear(); loginPasswordField.clear(); loginUrlField.clear();
        } catch (RuntimeException e) {
            messageLabel.setText("Errore durante il salvataggio nel DB.");
            System.err.println("Errore di salvataggio: " + e.getMessage());
        }
    }

    @FXML
    protected void onSaveCreditCardClick() {
        String description = cardDescriptionField.getText();
        String owner = cardOwnerField.getText();
        String bank = cardBankField.getText();
        String number = cardNumberField.getText();
        String cvv = cardCvvField.getText();
        String expiration = cardExpirationField.getText();

        if (creditCardManager == null || description.isEmpty() || number.isEmpty() || cvv.isEmpty() || expiration.isEmpty()) {
            messageLabel.setText("Descrizione, Numero Carta, CVV e Scadenza sono obbligatori.");
            return;
        }

        CreditCardItem newItem = new CreditCardItem(description, owner, bank, number, cvv, expiration);
        try {
            creditCardManager.criptaPoiSalva(newItem);
            handleSaveAndReload();

            messageLabel.setText("Carta salvata con successo: " + description);
            cardDescriptionField.clear(); cardOwnerField.clear(); cardBankField.clear(); cardNumberField.clear(); cardCvvField.clear(); cardExpirationField.clear();
        } catch (RuntimeException e) {
            messageLabel.setText("Errore durante il salvataggio nel DB.");
            System.err.println("Errore di salvataggio: " + e.getMessage());
        }
    }

    // -- GESTIONE DETTAGLI (Decrittografia in tempo reale) --

    private void clearDetails() {
        detailUsernameTitle.setVisible(false); detailUsernameLabel.setVisible(false);
        detailPasswordTitle.setVisible(false); detailPasswordLabel.setVisible(false);
        detailUrlTitle.setVisible(false); detailUrlLabel.setVisible(false);
        detailOwnerTitle.setVisible(false); detailOwnerLabel.setVisible(false);
        detailBankTitle.setVisible(false); detailBankLabel.setVisible(false);
        detailNumberTitle.setVisible(false); detailNumberLabel.setVisible(false);
        detailCvvTitle.setVisible(false); detailCvvLabel.setVisible(false);
        detailExpirationTitle.setVisible(false); detailExpirationLabel.setVisible(false);

        detailDescriptionLabel.setText("");
        detailTypeLabel.setText("");
        detailMessageLabel.setText("Seleziona un elemento per visualizzare i dettagli.");
    }

    private void showDetails(Item item) {
        clearDetails();

        try {
            Item decryptedItem;

            if (item.getItemType() == com.simonepugliese.Item.ItemType.LOGIN) {
                decryptedItem = loginCriptor.decripta(item);
            } else if (item.getItemType() == com.simonepugliese.Item.ItemType.CREDITCARD) {
                decryptedItem = creditCardCriptor.decripta(item);
            } else {
                detailMessageLabel.setText("Tipo di elemento non supportato.");
                return;
            }

            resetTimeout();

            // 1. Visualizza i campi comuni
            detailDescriptionLabel.setText(decryptedItem.getDescription());
            detailTypeLabel.setText(decryptedItem.getItemType().toString());
            detailMessageLabel.setText("Dettagli decriptati in chiaro. Attenzione al timeout!");

            // 2. Visualizza i campi specifici
            if (decryptedItem instanceof com.simonepugliese.Item.LoginItem loginItem) {
                detailUsernameTitle.setVisible(true); detailUsernameLabel.setVisible(true);
                detailUsernameLabel.setText(loginItem.getUsername());

                detailPasswordTitle.setVisible(true); detailPasswordLabel.setVisible(true);
                detailPasswordLabel.setText(loginItem.getPassword());

                detailUrlTitle.setVisible(true); detailUrlLabel.setVisible(true);
                detailUrlLabel.setText(loginItem.getUrlSito());

            } else if (decryptedItem instanceof com.simonepugliese.Item.CreditCardItem creditCardItem) {
                detailOwnerTitle.setVisible(true); detailOwnerLabel.setVisible(true);
                detailOwnerLabel.setText(creditCardItem.getOwner());

                detailBankTitle.setVisible(true); detailBankLabel.setVisible(true);
                detailBankLabel.setText(creditCardItem.getBank());

                detailNumberTitle.setVisible(true); detailNumberLabel.setVisible(true);
                detailNumberLabel.setText(creditCardItem.getNumber()); // DATO SENSIBILE IN CHIARO

                detailCvvTitle.setVisible(true); detailCvvLabel.setVisible(true);
                detailCvvLabel.setText(creditCardItem.getCvv()); // DATO SENSIBILE IN CHIARO

                detailExpirationTitle.setVisible(true); detailExpirationLabel.setVisible(true);
                detailExpirationLabel.setText(creditCardItem.getExpiration());
            }

        } catch (RuntimeException e) {
            detailMessageLabel.setText("ERRORE DI DECRITTOGRAFIA. Forza blocco.");
            System.err.println("Errore di decrittografia su selezione: " + e.getMessage());
            lockApplication();
            //ciao
        }
    }
}