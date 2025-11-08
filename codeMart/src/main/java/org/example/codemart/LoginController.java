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
import java.util.List;

import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    public static String loggedInUser = null;

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password are required.");
            return;
        }

        boolean success = DatabaseConnection.loginUser(username, password);

        if (success) {
            loggedInUser = username;
            statusLabel.setText("Login Successful! Proceeding to categories...");
            try {
                Stage stage = (Stage) statusLabel.getScene().getWindow();
                loadCategoryScene(stage);
            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("Error loading application main menu");
            }
        } else {
            statusLabel.setText("Login Failed: Invalid username or password.");
        }
    }

    private void loadCategoryScene(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/codemart/category.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Product Categories");
        stage.show();
    }

    @FXML
    protected void onCreateAccountButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/codemart/register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("CodeMart - Register");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onAdminButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/codemart/admin-password.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Admin Login - Inventory Access");
        stage.setScene(scene);
        stage.show();
    }

    public static void saveCheckoutToDatabase(String username, List<CategoryController.Product> cartItems, double total, String paymentMethod) {
        try {
            MongoCollection<Document> checkouts = DatabaseConnection.getDatabase().getCollection("checkouts");

            for (CategoryController.Product product : cartItems) {
                Document doc = new Document("username", username)
                        .append("product_name", product.name)
                        .append("quantity", product.quantity)
                        .append("price", product.price)
                        .append("total", product.price * product.quantity)
                        .append("payment_method", paymentMethod);
                checkouts.insertOne(doc);
            }

            System.out.println("✅ Checkout saved to database for user: " + username);
        } catch (Exception e) {
            System.err.println("⚠️ Error saving checkout: " + e.getMessage());
        }
    }
}
