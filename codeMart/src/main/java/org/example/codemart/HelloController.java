package org.example.codemart;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloController    {

    // --- LOGIN VIEW FIELDS (hello-view.fxml) ---
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    // --- REGISTER VIEW FIELDS (register-view.fxml) ---
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private Label regStatusLabel;



    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        DatabaseConnection.initialize();

        boolean loggedIn = DatabaseConnection.loginUser(username, password);

        if (loggedIn) {
            statusLabel.setText("Login Successful! Welcome, " + username + "!");
        } else {
            statusLabel.setText("Login Failed! Invalid credentials.");
        }
    }

    @FXML
    protected void onCreateAccountButtonClick() {
        // Switches from login screen to register screen
        loadScene("register-view.fxml");
    }


    // ---------------------- REGISTRATION HANDLERS ----------------------

    @FXML
    protected void onRegisterButtonClick() {
        String username = regUsernameField.getText();
        String password = regPasswordField.getText();
        String confirmPassword = regConfirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            regStatusLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            regStatusLabel.setText("Error: Passwords do not match!");
            return;
        }

        DatabaseConnection.initialize();

        boolean registered = DatabaseConnection.registerUser(username, password);

        if (registered) {
            regStatusLabel.setText("Account created! Returning to Login.");
            loadScene("hello-view.fxml"); // Success: switch back to login
        } else {
            regStatusLabel.setText("Registration failed! User may already exist.");
        }
    }

    @FXML
    protected void onBackToLoginButtonClick() {
        // Switches from register screen back to login screen
        loadScene("hello-view.fxml");
    }

    // ---------------------- SCENE SWITCHING UTILITY ----------------------

    private void loadScene(String fxmlFile) {
        try {
            // NOTE: You may need to adjust the path to the FXML resource depending on your exact setup.
            FXMLLoader fxmlLoader = new FXMLLoader(HelloController.class.getResource("/org/example/codemart/" + fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());

            // Get the current stage from an initialized element
            Stage stage = (Stage) (usernameField != null ? usernameField.getScene().getWindow() : regUsernameField.getScene().getWindow());

            stage.setScene(scene);
            stage.setTitle(fxmlFile.equals("hello-view.fxml") ? "User Login" : "New Account Registration");
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error during scene switch: " + e.getMessage());
            e.printStackTrace();
        }
    }
}