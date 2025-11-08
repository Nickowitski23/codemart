package org.example.codemart;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPasswordController {

    @FXML
    private PasswordField adminPasswordField;

    @FXML
    private Label statusLabel;

    // Admin password
    private final String ADMIN_PASSWORD = "admin123";

    @FXML
    protected void onProceedButtonClick(ActionEvent event) throws IOException {
        String enteredPassword = adminPasswordField.getText();

        if (enteredPassword.isEmpty()) {
            statusLabel.setText("Password is required.");
            return;
        }

        if (enteredPassword.equals(ADMIN_PASSWORD)) {
            // Load inventory.fxml
            Stage stage = (Stage) adminPasswordField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/codemart/inventory.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

            stage.setScene(scene);
            stage.setTitle("Admin Inventory");
            stage.show();
        } else {
            statusLabel.setText("Incorrect password. Try again.");
        }
    }

    @FXML
    protected void onCancelButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) adminPasswordField.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/codemart/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        stage.setScene(scene);
        stage.setTitle("CodeMart - Login");
        stage.show();
    }
}
