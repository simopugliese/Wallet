package com.simonepugliese;

import com.simonepugliese.Item.Item;
import com.simonepugliese.Manager.Manager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller per la Home Screen (Menu principale) dopo l'autenticazione.
 * Gestisce la navigazione e il Logout.
 */
public class HomeController {

    private Manager loginManager;
    private Manager creditCardManager;
    private List<Item> initialItems;
    private TimeoutController timeoutController;

    @FXML
    public void initialize() {
        // Inizializza il TimeoutController con l'azione di lock
        timeoutController = new TimeoutController(this::lockApplication);
    }

    // ============================== INIEZIONE DIPENDENZE ==============================

    public void setupManagers(Manager loginManager, Manager creditCardManager) {
        this.loginManager = loginManager;
        this.creditCardManager = creditCardManager;
    }

    public void setInitialItems(List<Item> initialItems) {
        this.initialItems = initialItems;
    }

    public void setupInteractionListener(Scene scene) {
        timeoutController.startTimeout();
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> timeoutController.resetTimeout());
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> timeoutController.resetTimeout());
    }

    // ============================== NAVIGAZIONE ==============================

    /**
     * Metodo generico per caricare la schermata di gestione (ManagementController/ManagementView.fxml).
     */
    private void navigateToManagementScreen(String title, String initialTabId) {
        try {
            Stage stage = WalletApplication.getPrimaryStage();

            // *** CORREZIONE QUI ***: Uso di getClass() e nome file corretto
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ManagementView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            ManagementController managementController = fxmlLoader.getController();

            // Inietta i Manager e i Dati nel controller di gestione
            managementController.setupManagers(loginManager, creditCardManager);
            managementController.unlockApplication(initialItems, scene, initialTabId);

            stage.setScene(scene);
            stage.setTitle("Wallet Manager - " + title);

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata di Gestione: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Errore di Navigazione", "Impossibile caricare la schermata di gestione. Controlla il nome FXML (ManagementView.fxml) e il percorso.");
        }
    }

    @FXML
    protected void onLoginTileClicked() {
        navigateToManagementScreen("Logins & Passwords", "loginTab");
    }

    @FXML
    protected void onCreditCardTileClicked() {
        navigateToManagementScreen("Carte di Credito", "cardTab");
    }

    @FXML
    protected void onWifiTileClicked() {
        showAlert(AlertType.INFORMATION, "Work in Progress", "La sezione WiFi è in fase di sviluppo.");
        // Non navighiamo se non è pronto: navigateToManagementScreen("WiFi", "wifiTab");
    }

    @FXML
    protected void onAppTileClicked() {
        showAlert(AlertType.INFORMATION, "Work in Progress", "La sezione Applicazioni è in fase di sviluppo.");
    }

    // ============================== LOGOUT / SICUREZZA ==============================

    @FXML
    protected void onLogoutClick() {
        if (showConfirmationDialog("Conferma Logout", "Sei sicuro di voler uscire? La Master Password sarà rimossa dalla memoria.")) {
            lockApplication();
        }
    }

    /**
     * Blocca l'applicazione (chiamata dal timeout o dal logout manuale).
     */
    public void lockApplication() {
        timeoutController.stopTimeout();
        com.simonepugliese.Criptor.CryptoUtils.masterPassSet("");

        Platform.runLater(() -> {
            try {
                Stage stage = WalletApplication.getPrimaryStage();

                // Uso di getClass().getResource() per coerenza
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Scene loginScene = new Scene(fxmlLoader.load(), 600, 400);

                stage.setScene(loginScene);
                stage.setTitle("Wallet Manager Login");

            } catch (IOException e) {
                System.err.println("Errore nel ricaricamento della scena di login: " + e.getMessage());
            }
        });
    }

    // ============================== UTILITY ==============================

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
