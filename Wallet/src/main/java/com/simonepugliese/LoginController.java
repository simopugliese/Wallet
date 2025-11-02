package com.simonepugliese;

import com.simonepugliese.Criptor.CreditCardCriptor;
import com.simonepugliese.Criptor.CryptoUtils;
import com.simonepugliese.Criptor.LoginItemCriptor;
import com.simonepugliese.Item.Item;
import com.simonepugliese.Manager.Manager;
import com.simonepugliese.Saver.CreditCardSaver;
import com.simonepugliese.Saver.DbConnector;
import com.simonepugliese.Saver.LoginItemSaver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML private PasswordField masterPasswordField;
    @FXML private Label messageLabel;
    @FXML private Button selectDbButton;
    @FXML private Label dbPathLabel;

    private File walletDbFile;
    private File lastUsedDirectory = new File(System.getProperty("user.home"));

    private Manager loginManager;
    private Manager creditCardManager;

    @FXML
    public void initialize() {
        messageLabel.setText("Seleziona il tuo database.");
    }

    @FXML
    protected void onSelectDbButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona il tuo Wallet Database (.db)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SQLite Database", "*.db")
        );

        // Imposta la directory iniziale
        if (lastUsedDirectory.exists() && lastUsedDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(lastUsedDirectory);
        }

        Stage currentStage = (Stage) selectDbButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(currentStage);

        if (selectedFile != null) {
            walletDbFile = selectedFile;
            dbPathLabel.setText("DB Selezionato: " + walletDbFile.getName());

            DbConnector.setJdbcUrl(walletDbFile.getAbsolutePath());
            messageLabel.setText("Inserisci la Master Password.");
            messageLabel.setStyle("-fx-text-fill: black;");

            // Aggiorna la directory usata
            lastUsedDirectory = selectedFile.getParentFile();
        }
    }

    @FXML
    protected void onLoginButtonClick() {
        String masterPass = masterPasswordField.getText();

        if (walletDbFile == null || !walletDbFile.exists()) {
            messageLabel.setText("ERRORE: Devi prima selezionare il file Wallet DB.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (masterPass.isEmpty()) {
            messageLabel.setText("Inserisci la Master Password.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        CryptoUtils.masterPassSet(masterPass);

        try {
            // 1. Inizializza i Manager
            loginManager = new Manager(new LoginItemCriptor(), new LoginItemSaver());
            creditCardManager = new Manager(new CreditCardCriptor(), new CreditCardSaver());

            // 2. Tentativo di decrittografia (test di login)
            List<Item> initialItems = loginManager.caricaPoiDecripta();
            initialItems.addAll(creditCardManager.caricaPoiDecripta());

            // 3. Login Riuscito: Passa alla Home Screen
            messageLabel.setText("Accesso Riuscito!");
            messageLabel.setStyle("-fx-text-fill: green;");
            switchToHomeScreen(initialItems);

        } catch (RuntimeException e) {
            System.err.println("Errore di Login: " + e.getMessage());
            messageLabel.setText("Password errata o file DB corrotto.");
            messageLabel.setStyle("-fx-text-fill: red;");
            masterPasswordField.clear();
            CryptoUtils.masterPassSet("");
        }
    }

    /**
     * Carica la Home Screen (home.fxml) e passa i Manager e i dati iniziali.
     */
    private void switchToHomeScreen(List<Item> initialItems) {
        try {
            Stage stage = WalletApplication.getPrimaryStage();

            FXMLLoader fxmlLoader = new FXMLLoader(WalletApplication.class.getResource("/home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            HomeController homeController = fxmlLoader.getController();
            homeController.setupManagers(loginManager, creditCardManager);
            homeController.setInitialItems(initialItems);
            homeController.setupInteractionListener(scene);

            stage.setTitle("Wallet Manager - Home");
            stage.setScene(scene);

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della Home Screen: " + e.getMessage());
        }
    }
}
