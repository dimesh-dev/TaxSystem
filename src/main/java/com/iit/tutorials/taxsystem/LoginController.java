package com.iit.tutorials.taxsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private void handleEnter() {
        try {
            // Load the main application
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/iit/tutorials/taxsystem/tax-system.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) Stage.getWindows().stream().filter(window -> window instanceof Stage).findFirst().orElse(null);
            if (stage != null) {
                stage.setScene(new Scene(root, 827, 615));
                stage.setTitle("DM SugarCraft Tax System");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}