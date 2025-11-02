package com.simonepugliese;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WalletApplication extends Application {

    // Lo Stage viene reso accessibile staticamente per poterlo riutilizzare
    // nel lockApplication/navigazione.
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // 1. Carica la scena iniziale (Login)
        FXMLLoader fxmlLoader = new FXMLLoader(WalletApplication.class.getResource("/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        stage.setTitle("Wallet Manager Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Fornisce accesso allo Stage principale.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
