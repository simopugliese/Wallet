package com.simonepugliese;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WalletApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WalletApplication.class.getResource("/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        WalletController controller = fxmlLoader.getController();

        controller.setupInteractionListener(scene);

        stage.setTitle("Wallet Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}