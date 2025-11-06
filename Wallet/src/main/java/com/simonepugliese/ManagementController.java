package com.simonepugliese;

import com.simonepugliese.Criptor.CreditCardCriptor;
import com.simonepugliese.Criptor.CryptoUtils;
import com.simonepugliese.Criptor.LoginItemCriptor;
import com.simonepugliese.Item.CreditCardItem;
import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.LoginItem;
import com.simonepugliese.Manager.Manager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la schermata di Gestione (ex wallet.fxml).
 * Gestisce l'interfaccia con tabelle, dettagli e form di salvataggio.
 */
public class ManagementController {

    // Componenti UI per Dashboard
    @FXML private TabPane mainTabPane;
    @FXML private TableView<Item> itemTable;
    @FXML private TableColumn<Item, String> descriptionColumn;
    @FXML private TableColumn<Item, String> typeColumn;

    // Componenti UI per Form Salvataggio (Login)
    @FXML private TextField loginDescriptionField;
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
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

    // Logica e Dipendenze
    private Manager loginManager;
    private Manager creditCardManager;
    private final LoginItemCriptor loginCriptor = new LoginItemCriptor();
    private final CreditCardCriptor creditCardCriptor = new CreditCardCriptor();
    private final ObservableList<Item> walletItems = FXCollections.observableArrayList();
    private TimeoutController timeoutController;

    @FXML
    public void initialize() {
        // Inizializzazione della tabella
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        itemTable.setItems(walletItems);

        clearDetails();

        // Listener per i dettagli
        itemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDetails(newSelection);
            } else {
                clearDetails();
            }
        });

        // Inizializza il TimeoutController con l'azione di lock
        timeoutController = new TimeoutController(this::lockApplication);
    }

    // =========================================================================
    // METODI DI INIEZIONE E SBLOCCO
    // =========================================================================

    public void setupManagers(Manager loginManager, Manager creditCardManager) {
        this.loginManager = loginManager;
        this.creditCardManager = creditCardManager;
    }

    /**
     * Sblocca l'interfaccia e imposta i dati iniziali.
     */
    public void unlockApplication(List<Item> initialItems, Scene scene, String initialTabId) {
        // Popola l'ObservableList con i dati iniziali
        walletItems.setAll(initialItems);

        // Se specificato, seleziona il Tab corretto all'apertura
        if (initialTabId != null && !initialTabId.isEmpty()) {
            for (Tab tab : mainTabPane.getTabs()) {
                if (tab.getText().toLowerCase().contains(initialTabId.toLowerCase())) {
                    mainTabPane.getSelectionModel().select(tab);
                    break;
                }
            }
        }

        // Avvia il monitoraggio del timeout e imposta i listener
        timeoutController.startTimeout();
        setupInteractionListener(scene);
    }

    /**
     * Imposta i listener di interazione sulla Scene per resettare il timeout.
     */
    private void setupInteractionListener(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> timeoutController.resetTimeout());
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> timeoutController.resetTimeout());
        itemTable.setOnMousePressed(e -> timeoutController.resetTimeout());
    }

    // =========================================================================
    // GESTIONE BLOCCO/LOGOUT
    // =========================================================================

    /**
     * Blocca l'applicazione e ricarica la scena di login (o torna alla Home in un sistema più complesso).
     */
    public void lockApplication() {
        timeoutController.stopTimeout();
        CryptoUtils.masterPassSet("");

        Platform.runLater(() -> {
            try {
                Stage stage = WalletApplication.getPrimaryStage();

                // Torna alla schermata di login
                FXMLLoader fxmlLoader = new FXMLLoader(WalletApplication.class.getResource("/login.fxml"));
                Scene loginScene = new Scene(fxmlLoader.load(), 600, 400);

                stage.setScene(loginScene);
                stage.setTitle("Wallet Manager Login");

            } catch (IOException e) {
                System.err.println("Errore nel ricaricamento della scena di login: " + e.getMessage());
            }
        });
    }

    @FXML
    protected void onLogoutClick() {
        try {
            Stage stage = WalletApplication.getPrimaryStage();

            // Torna alla Home Screen (per consentire un nuovo login)
            FXMLLoader fxmlLoader = new FXMLLoader(WalletApplication.class.getResource("/home.fxml"));
            Scene homeScene = new Scene(fxmlLoader.load(), 800, 600);

            HomeController homeController = fxmlLoader.getController();
            homeController.setupManagers(loginManager, creditCardManager);
            homeController.setInitialItems(new ArrayList<>(walletItems)); // Passa i dati decriptati

            stage.setScene(homeScene);
            stage.setTitle("Wallet Manager - Home");

        } catch (IOException e) {
            System.err.println("Errore nel ricaricamento della Home Screen: " + e.getMessage());
            showAlert(AlertType.ERROR, "Errore di Navigazione", "Impossibile tornare alla Home.");
        }
    }

    // =========================================================================
    // GESTIONE DATI (CRUD)
    // =========================================================================

    private void handleSaveAndReload(String successMessage) {
        try {
            // Ricarica tutti gli elementi (LoginItem e CreditCardItem)
            List<Item> updatedItems = loginManager.caricaPoiDecripta();
            updatedItems.addAll(creditCardManager.caricaPoiDecripta());

            walletItems.setAll(updatedItems);
            timeoutController.resetTimeout();

            showAlert(AlertType.INFORMATION, "Salvataggio Riuscito", successMessage);

        } catch (RuntimeException e) {
            System.err.println("ERRORE: Dati corrotti durante il ricaricamento. Blocco forzato.");
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
            showAlert(AlertType.WARNING, "Dati Incompleti", "Descrizione, Username e Password sono obbligatori.");
            return;
        }

        LoginItem newItem = new LoginItem(description, username, password, url);
        try {
            loginManager.criptaPoiSalva(newItem);

            handleSaveAndReload("Login salvato/aggiornato con successo: " + description);

            loginDescriptionField.clear();
            loginUsernameField.clear();
            loginPasswordField.clear();
            loginUrlField.clear();

        } catch (RuntimeException e) {
            showAlert(AlertType.ERROR, "Errore di Salvataggio", "Errore durante il salvataggio nel DB.");
            System.err.println("Errore di salvataggio LoginItem: " + e.getMessage());
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
            showAlert(AlertType.WARNING, "Dati Incompleti", "Descrizione, Numero Carta, CVV e Scadenza sono obbligatori.");
            return;
        }

        CreditCardItem newItem = new CreditCardItem(description, owner, bank, number, cvv, expiration);
        try {
            creditCardManager.criptaPoiSalva(newItem);

            handleSaveAndReload("Carta salvata/aggiornata con successo: " + description);

            cardDescriptionField.clear();
            cardOwnerField.clear();
            cardBankField.clear();
            cardNumberField.clear();
            cardCvvField.clear();
            cardExpirationField.clear();

        } catch (RuntimeException e) {
            showAlert(AlertType.ERROR, "Errore di Salvataggio", "Errore durante il salvataggio nel DB.");
            System.err.println("Errore di salvataggio CreditCardItem: " + e.getMessage());
        }
    }

    // =========================================================================
    // GESTIONE DETTAGLI (Decrittografia in tempo reale)
    // =========================================================================

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
        timeoutController.resetTimeout();

        try {
            Item decryptedItem = item;

            if (item.getItemType() == com.simonepugliese.Item.ItemType.LOGIN) {
                decryptedItem = loginCriptor.decripta(item);
                // TODO: Controlla qui
            } else if (item.getItemType() == com.simonepugliese.Item.ItemType.CREDITCARD) {
                decryptedItem = creditCardCriptor.decripta(item);
                // TODO: Controlla qui
            } else if (item.getItemType() == com.simonepugliese.Item.ItemType.WIFI) {
                // TODO: Implementare WifiCriptor e Manager
                detailMessageLabel.setText("Dettagli WiFi: Funzionalità in arrivo!");
                return;
            } else {
                detailMessageLabel.setText("Tipo di elemento non supportato.");
                return;
            }

            detailDescriptionLabel.setText(decryptedItem.getDescription());
            detailTypeLabel.setText(decryptedItem.getItemType().toString());
            detailMessageLabel.setText("Dettagli decriptati in chiaro. Attenzione al timeout!");

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
                detailNumberLabel.setText(creditCardItem.getNumber());

                detailCvvTitle.setVisible(true); detailCvvLabel.setVisible(true);
                detailCvvLabel.setText(creditCardItem.getCvv());

                detailExpirationTitle.setVisible(true); detailExpirationLabel.setVisible(true);
                detailExpirationLabel.setText(creditCardItem.getExpiration());
            }

        } catch (RuntimeException e) {
            detailMessageLabel.setText("ERRORE DI DECRITTOGRAFIA. Forza blocco.");
            System.err.println("Errore di decrittografia su selezione: " + e.getMessage());
            e.printStackTrace();
            lockApplication();
        }
    }

    // =========================================================================
    // UTILITY
    // =========================================================================

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
