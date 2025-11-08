package org.example.codemart;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class InventoryController {

    @FXML
    private TextField productNameField;

    @FXML
    private TextField productPriceField;

    @FXML
    private TextField productStockField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TableView<Product> inventoryTable;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, Integer> stockColumn;

    private ObservableList<Product> inventoryList = FXCollections.observableArrayList();

    public static class Product {
        private String name;
        private Double price;
        private String category;
        private int stock;

        public Product(String name, Double price, String category, int stock) {
            this.name = name;
            this.price = price;
            this.category = category;
            this.stock = stock;
        }

        public String getName() { return name; }
        public Double getPrice() { return price; }
        public String getCategory() { return category; }
        public int getStock() { return stock; }

        public void setPrice(Double price) { this.price = price; }
        public void setCategory(String category) { this.category = category; }
        public void setStock(int stock) { this.stock = stock; }
    }

    @FXML
    private void initialize() {

        categoryComboBox.getItems().addAll(
                "Food Essentials",
                "Drinks",
                "Snacks",
                "School Supplies",
                "Home Needs"
        );

        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        stockColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStock()).asObject());

        inventoryTable.setItems(inventoryList);

        // Enable editing
        inventoryTable.setEditable(true);
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        priceColumn.setOnEditCommit(event -> {
            Product p = event.getRowValue();
            p.setPrice(event.getNewValue());
            updateProductInDB(p);
            inventoryTable.refresh();
        });

        categoryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        categoryColumn.setOnEditCommit(event -> {
            Product p = event.getRowValue();
            p.setCategory(event.getNewValue());
            updateProductInDB(p);
            inventoryTable.refresh();
        });

        stockColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        stockColumn.setOnEditCommit(event -> {
            Product p = event.getRowValue();
            p.setStock(event.getNewValue());
            updateProductInDB(p);
            inventoryTable.refresh();
        });

        loadInventoryFromDB();
    }

    // Load all items from MongoDB
    private void loadInventoryFromDB() {
        inventoryList.clear();

        try {
            MongoCollection<Document> collection = DatabaseConnection.getDatabase().getCollection("inventory");

            MongoCursor<Document> cursor = collection.find().iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();

                String name = doc.getString("name");
                double price = doc.getDouble("price");
                String category = doc.getString("category");
                int stock = doc.getInteger("stock", 0);

                inventoryList.add(new Product(name, price, category, stock));
            }

        } catch (Exception e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }

    @FXML
    private void onAddItemClick() {

        String name = productNameField.getText();
        String priceText = productPriceField.getText();
        String category = categoryComboBox.getValue();
        String stockText = productStockField.getText();

        if (name.isEmpty() || priceText.isEmpty() || category == null || stockText.isEmpty()) {
            showAlert("Missing Input", "Please fill all fields.");
            return;
        }

        double price;
        int stock;

        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showAlert("Invalid Price", "Price must be a number.");
            return;
        }

        try {
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            showAlert("Invalid Stock", "Stock must be a number.");
            return;
        }

        try {
            MongoCollection<Document> collection = DatabaseConnection.getDatabase().getCollection("inventory");

            Document product = new Document("name", name)
                    .append("price", price)
                    .append("category", category)
                    .append("stock", stock);

            collection.insertOne(product);

            inventoryList.add(new Product(name, price, category, stock));

            productNameField.clear();
            productPriceField.clear();
            categoryComboBox.setValue(null);
            productStockField.clear();

            showAlert("Success", "Product added successfully!");

        } catch (Exception e) {
            showAlert("Database Error", "Error saving product.");
            System.err.println("❌ Inventory save error: " + e.getMessage());
        }
    }

    private void updateProductInDB(Product product) {
        try {
            MongoCollection<Document> collection = DatabaseConnection.getDatabase().getCollection("inventory");

            collection.updateOne(
                    new Document("name", product.getName()),
                    new Document("$set", new Document("price", product.getPrice())
                            .append("category", product.getCategory())
                            .append("stock", product.getStock()))
            );

            System.out.println("✅ Product updated in DB: " + product.getName());

        } catch (Exception e) {
            System.err.println("❌ Error updating product: " + e.getMessage());
        }
    }

    // Back to Login
    @FXML
    private void onBackClick() {
        try {
            Stage stage = (Stage) inventoryTable.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/codemart/login.fxml"));
            Scene scene = new Scene(loader.load(), 1280, 720);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error going back: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
