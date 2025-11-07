package org.example.codemart;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    // Handle Login button click
    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password are required.");
            return;
        }

        // Validate credentials using your database logic
        boolean success = DatabaseConnection.loginUser(username, password);

        if (success) {
            statusLabel.setText("Login Successful! Proceeding to categories...");
            try {
                // Navigate to category view
                Stage stage = (Stage) statusLabel.getScene().getWindow();
                loadCategoryScene(stage);
            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("Error loading application main menu. Check console for details.");
            }
        } else {
            statusLabel.setText("Login Failed: Invalid username or password.");
        }
    }

    // Transition to category view after successful login
    private void loadCategoryScene(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/codemart/category.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Product Categories");
        stage.show();
    }

    // Handle "Create Account" button click (Go to Register)
    @FXML
    protected void onCreateAccountButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/codemart/register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 520);
        stage.setTitle("CodeMart - Register");
        stage.setScene(scene);
        stage.show();
    }
}
