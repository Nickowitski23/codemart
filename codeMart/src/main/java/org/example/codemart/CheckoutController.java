package org.example.codemart;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckoutController {

    @FXML
    private VBox checkoutCartVBox;

    @FXML
    private Label checkoutTotalLabel;

    @FXML
    private ComboBox<String> paymentComboBox;

    private double total = 0;

    @FXML
    private void initialize() {
        // Populate payment options
        paymentComboBox.getItems().addAll("Cash", "Gcash", "Credit/Debit Card", "PayMaya");
        populateCart();
    }

    private void populateCart() {
        checkoutCartVBox.getChildren().clear();
        total = 0;

        for (CategoryController.Product product : CategoryController.getGlobalCart()) {
            HBox itemBox = new HBox(20);
            Label nameLabel = new Label(product.name);
            Label quantityLabel = new Label("Qty: " + product.quantity);
            Label priceLabel = new Label("₱" + String.format("%.2f", product.price * product.quantity));
            itemBox.getChildren().addAll(nameLabel, quantityLabel, priceLabel);
            checkoutCartVBox.getChildren().add(itemBox);

            total += product.price * product.quantity;
        }

        checkoutTotalLabel.setText("₱" + String.format("%.2f", total));
    }

    @FXML
    protected void onBackToCategoriesButtonClick(ActionEvent event) throws Exception {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("category.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 720);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Categories");
        stage.show();
    }

    @FXML
    protected void onConfirmPaymentButtonClick(ActionEvent event) {
        String paymentMethod = paymentComboBox.getValue();

        if (paymentMethod == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please select a payment method!");
            alert.showAndWait();
            return;
        }

        // Generate a unique transaction ID
        String transactionId = UUID.randomUUID().toString();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Payment Successful!");
        alert.setContentText(
                "Transaction ID: " + transactionId +
                        "\nTotal: ₱" + String.format("%.2f", total) +
                        "\nPayment Method: " + paymentMethod
        );
        alert.showAndWait();

        String username = LoginController.loggedInUser; // Get logged-in username
        if (username != null && !CategoryController.getGlobalCart().isEmpty()) {
            DatabaseConnection.saveOrder(username, new ArrayList<>(CategoryController.getGlobalCart()), total, paymentMethod, transactionId);
        }

        for (CategoryController.Product product : CategoryController.getGlobalCart()) {
            try {
                var collection = DatabaseConnection.getDatabase().getCollection("inventory");

                var doc = collection.find(new org.bson.Document("name", product.name)).first();
                if (doc != null) {
                    int currentStock = doc.getInteger("stock", 0);
                    int newStock = Math.max(currentStock - product.quantity, 0);

                    collection.updateOne(
                            new org.bson.Document("name", product.name),
                            new org.bson.Document("$set", new org.bson.Document("stock", newStock))
                    );
                }
            } catch (Exception e) {
                System.err.println("Error updating stock for product " + product.name + ": " + e.getMessage());
            }
        }

        // Clear the global cart after successful checkout
        CategoryController.getGlobalCart().clear();
    }
}
