package org.example.codemart;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoodessentialsController {

    @FXML
    private VBox cartItemsVBox;
    @FXML
    private Label cartTotalLabel;

    // Make the cart static so it persists between scene switches
    private static final List<Product> shoppingCart = new ArrayList<>();
    private static double cartTotal = 0.0;

    // Inner class to represent a product
    private static class Product {
        String name;
        double price;
        int quantity;

        public Product(String name, double price) {
            this.name = name;
            this.price = price;
            this.quantity = 1;
        }
    }

    // Called automatically when FXML is loaded
    @FXML
    private void initialize() {
        updateCartDisplay(); // ✅ Ensures cart is shown immediately after returning
    }

    // Handle "Add to Cart" button click
    @FXML
    protected void onAddToCartButtonClick(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String productData = (String) sourceButton.getUserData();
        String[] productDetails = productData.split("\\|");
        String productName = productDetails[0];
        double productPrice = Double.parseDouble(productDetails[1]);

        boolean found = false;
        for (Product product : shoppingCart) {
            if (product.name.equals(productName)) {
                product.quantity++;
                found = true;
                break;
            }
        }

        if (!found) {
            shoppingCart.add(new Product(productName, productPrice));
        }

        updateCartDisplay();
    }

    // Update the cart display
    private void updateCartDisplay() {
        cartItemsVBox.getChildren().clear();
        cartTotal = 0.0;

        for (Product product : shoppingCart) {
            cartTotal += product.price * product.quantity;

            Label nameLabel = new Label(product.name);
            Label quantityLabel = new Label("Quantity: " + product.quantity);
            Label priceLabel = new Label("₱" + String.format("%.2f", product.price * product.quantity));

            // Create a decrement button for each product
            Button decrementButton = new Button("-");
            decrementButton.setOnAction(e -> {
                if (product.quantity > 1) {
                    product.quantity--;
                } else {
                    shoppingCart.remove(product);
                }
                updateCartDisplay();
            });

            // Optional: add an increment button for convenience
            Button incrementButton = new Button("+");
            incrementButton.setOnAction(e -> {
                product.quantity++;
                updateCartDisplay();
            });

            HBox controlsBox = new HBox(10, decrementButton, quantityLabel, incrementButton);
            VBox itemBox = new VBox(5, nameLabel, priceLabel, controlsBox);
            itemBox.setStyle("-fx-padding: 10; -fx-background-color: #E8F5E9; -fx-background-radius: 10;");

            cartItemsVBox.getChildren().add(itemBox);
        }

        cartTotalLabel.setText("₱" + String.format("%.2f", cartTotal));
    }

    // Handle "Back to Categories" button
    @FXML
    protected void onBackToCategoriesButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("category.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Categories");
        stage.show();
    }
}
