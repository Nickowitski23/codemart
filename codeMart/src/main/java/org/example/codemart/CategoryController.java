package org.example.codemart;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class CategoryController {

    // Handle Food Essentials button click
    @FXML
    protected void onFoodEssentialsButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("foodessentials-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Food Essentials");
        stage.show();
    }

    // Handle Drinks button click
    @FXML
    protected void onDrinksButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("drinks-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Drinks");
        stage.show();
    }

    // Handle Snacks button click
    @FXML
    protected void onSnacksButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("snacks-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Snacks");
        stage.show();
    }

    // Handle School Supplies button click
    @FXML
    protected void onSchoolSuppliesButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("schoolsupplies-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        stage.setScene(scene);
        stage.setTitle("CodeMart - School Supplies");
        stage.show();
    }

    // Handle Home Needs button click
    @FXML
    protected void onHomeNeedsButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homeneeds-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Home Needs");
        stage.show();
    }

    // ✅ Handle Logout button click — return to Login page
    @FXML
    protected void onLogoutButtonClick(ActionEvent event) throws IOException {
        // Load login page (make sure login.fxml exists in the same folder as other views)
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml")); // ✅ Fixed file name
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);
        stage.setScene(scene);
        stage.setTitle("CodeMart - Login");
        stage.show();
    }
}
