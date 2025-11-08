package org.example.codemart;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class CategoryController {

    @FXML
    private VBox cartItemsVBox; // Shared cart display
    @FXML
    private Label cartTotalLabel;

    // Shared global cart across all categories
    public static class Product {
        public String name;
        public double price;
        public int quantity;

        public Product(String name, double price) {
            this.name = name;
            this.price = price;
            this.quantity = 1;
        }
    }

    private static final List<Product> globalCart = new ArrayList<>();

    // Add a product to the global cart with stock check
    public static void addToGlobalCart(String name, double price) {
        try {
            MongoCollection<Document> collection = DatabaseConnection.getDatabase().getCollection("inventory");
            Document productDoc = collection.find(new Document("name", name)).first();

            if (productDoc == null) {
                showNoStockAlert(name);
                return;
            }

            int stock = productDoc.getInteger("stock", 0);
            if (stock < 1) {
                showNoStockAlert(name);
                return;
            }

            for (Product p : globalCart) {
                if (p.name.equals(name)) {
                    if (p.quantity < stock) {
                        p.quantity++;
                    } else {
                        showNoStockAlert(name);
                    }
                    return;
                }
            }

            globalCart.add(new Product(name, price));

        } catch (Exception e) {
            System.err.println("⚠️ Error checking stock for " + name + ": " + e.getMessage());
        }
    }

    // Alert for no stock
    private static void showNoStockAlert(String productName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Out of Stock");
        alert.setContentText("Sorry, \"" + productName + "\" is out of stock!");
        alert.showAndWait();
    }

    public static void removeOneFromGlobalCart(Product product) {
        if (product.quantity > 1) product.quantity--;
        else globalCart.remove(product);
    }

    public static List<Product> getGlobalCart() {
        return globalCart;
    }

    public static double getGlobalCartTotal() {
        double total = 0;
        for (Product p : globalCart) total += p.price * p.quantity;
        return total;
    }

    @FXML
    private void initialize() {
        updateCartDisplay();
    }

    public void updateCartDisplay() {
        if (cartItemsVBox == null || cartTotalLabel == null) return;

        cartItemsVBox.getChildren().clear();
        for (Product product : globalCart) {
            Button decrementButton = new Button("-");
            decrementButton.setOnAction(e -> {
                removeOneFromGlobalCart(product);
                updateCartDisplay();
            });

            Button incrementButton = new Button("+");
            incrementButton.setOnAction(e -> {
                addToGlobalCart(product.name, product.price); // Updated to check stock on increment
                updateCartDisplay();
            });

            Label quantityLabel = new Label("Qty: " + product.quantity);
            Label priceLabel = new Label("₱" + String.format("%.2f", product.price * product.quantity));
            Label nameLabel = new Label(product.name);

            HBox controlsBox = new HBox(5, decrementButton, quantityLabel, incrementButton);
            VBox itemBox = new VBox(5, nameLabel, priceLabel, controlsBox);
            itemBox.setStyle("-fx-padding: 10; -fx-background-color: #E8F5E9; -fx-background-radius: 10;");
            cartItemsVBox.getChildren().add(itemBox);
        }
        cartTotalLabel.setText("₱" + String.format("%.2f", getGlobalCartTotal()));
    }

    public static void reduceStock(String productName, int quantityPurchased) {
        try {
            MongoCollection<Document> collection = DatabaseConnection.getDatabase().getCollection("inventory");

            Document product = collection.find(new Document("name", productName)).first();

            if (product != null) {
                int currentStock = product.getInteger("stock", 0);
                int newStock = Math.max(currentStock - quantityPurchased, 0);

                collection.updateOne(
                        new Document("name", productName),
                        new Document("$set", new Document("stock", newStock))
                );
                System.out.println("✅ Stock updated for " + productName + ": " + newStock + " left.");
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error updating stock for " + productName + ": " + e.getMessage());
        }
    }

    @FXML
    protected void onClearCartButtonClick() {
        if (globalCart.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Cart is already empty");
            alert.setContentText("There are no items to clear.");
            alert.showAndWait();
            return;
        }

        globalCart.clear();
        updateCartDisplay();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Cart Cleared");
        alert.setContentText("All items have been removed from your cart.");
        alert.showAndWait();
    }

    @FXML
    protected void onFoodEssentialsButtonClick(ActionEvent event) throws IOException {
        switchScene(event, "foodessentials-view.fxml", "CodeMart - Food Essentials");
    }

    @FXML
    protected void onDrinksButtonClick(ActionEvent event) throws IOException {
        switchScene(event, "Drinks.fxml", "CodeMart - Drinks");
    }

    @FXML
    protected void onSnacksButtonClick(ActionEvent event) throws IOException {
        switchScene(event, "snacks.fxml", "CodeMart - Snacks");
    }

    @FXML
    protected void onSchoolSuppliesButtonClick(ActionEvent event) throws IOException {
        switchScene(event, "schoolsupplies.fxml", "CodeMart - School Supplies");
    }

    @FXML
    protected void onHomeNeedsButtonClick(ActionEvent event) throws IOException {
        switchScene(event, "homeneeds.fxml", "CodeMart - Home Needs");
    }

    @FXML
    protected void onLogoutButtonClick(ActionEvent event) throws IOException {
        switchScene(event, "login.fxml", "CodeMart - Login", 1280, 720);
    }

    @FXML
    protected void onCheckoutButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("checkout.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 720);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Checkout");
        stage.show();
    }

    private void switchScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        switchScene(event, fxmlFile, title, 1280, 720);
    }

    private void switchScene(ActionEvent event, String fxmlFile, String title, int width, int height) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(loader.load(), width, height);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
}
