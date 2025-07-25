package com.iit.tutorials.taxsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TaxSystemApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login screen first
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/iit/tutorials/taxsystem/login.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Tax System - Login");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}