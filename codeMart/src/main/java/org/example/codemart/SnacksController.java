package org.example.codemart;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SnacksController {

    @FXML
    private VBox cartItemsVBox;
    @FXML
    private Label cartTotalLabel;

    @FXML
    private void initialize() {
        updateCartDisplay();
    }

    @FXML
    protected void onAddToCartButtonClick(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String[] data = sourceButton.getUserData().toString().split("\\|");
        String name = data[0];
        double price = Double.parseDouble(data[1]);

        CategoryController.addToGlobalCart(name, price);
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        cartItemsVBox.getChildren().clear();

        for (CategoryController.Product product : CategoryController.getGlobalCart()) {
            Label nameLabel = new Label(product.name);
            Label quantityLabel = new Label("Qty: " + product.quantity);
            Label priceLabel = new Label("₱" + String.format("%.2f", product.price * product.quantity));

            Button decrementButton = new Button("-");
            decrementButton.setOnAction(e -> {
                CategoryController.removeOneFromGlobalCart(product);
                updateCartDisplay();
            });

            Button incrementButton = new Button("+");
            incrementButton.setOnAction(e -> {
                CategoryController.addToGlobalCart(product.name, product.price);
                updateCartDisplay();
            });

            HBox controlsBox = new HBox(10, decrementButton, quantityLabel, incrementButton);
            VBox itemBox = new VBox(5, nameLabel, priceLabel, controlsBox);
            itemBox.setStyle("-fx-padding: 10; -fx-background-color: #FFF3E0; -fx-background-radius: 10;");

            cartItemsVBox.getChildren().add(itemBox);
        }

        cartTotalLabel.setText("₱" + String.format("%.2f", CategoryController.getGlobalCartTotal()));
    }

    @FXML
    protected void onBackToCategoriesButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("category.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 720);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Categories");
        stage.show();
    }
}
