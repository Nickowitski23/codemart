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
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private TextField regUsernameField;

    @FXML
    private TextField regEmailField;

    @FXML
    private PasswordField regPasswordField;

    @FXML
    private PasswordField regConfirmPasswordField;

    @FXML
    private Label regStatusLabel;

    @FXML
    private Button backToLoginButton;

    // Regex pattern for simple email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    // Handle registration
    @FXML
    protected void onRegisterButtonClick() {
        String username = regUsernameField.getText();
        String email = regEmailField.getText();
        String password = regPasswordField.getText();
        String confirmPassword = regConfirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            regStatusLabel.setText("All fields are required.");
            return;
        }

        // ✅ Email format validation
        if (!isValidEmail(email)) {
            regStatusLabel.setText("Invalid email format. Use user@example.com");
            return;
        }

        if (!password.equals(confirmPassword)) {
            regStatusLabel.setText("Passwords do not match.");
            return;
        }

        boolean success = DatabaseConnection.registerUser(username, email, password);
        if (success) {
            regStatusLabel.setText("Registration complete! Please log in.");
        } else {
            regStatusLabel.setText("Registration failed. Try a different username/email.");
        }
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @FXML
    protected void onBackToLoginButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/codemart/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("CodeMart - Login");
        stage.setScene(scene);
        stage.show();
    }
}
