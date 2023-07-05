package com.example.c195final.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MainScreenController {

    @FXML
    public void loginButtonAction(ActionEvent event) throws IOException {
        // Store username and password into variables
        String username = usernameField.getText();
        String password = passwordField.getText();

        LoginController userAuthentication = new LoginController();
        if (userAuthentication.isValidCredentials(username, password)) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/c195final/MainScreen.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            MainScreenController controller = loader.getController();
            // controller.sendProduct(productTableView.getSelectionModel().getSelectedIndex(),productTableView.getSelectionModel().getSelectedItem());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    @FXML
    public void loginButtonAction(ActionEvent event) throws IOException {
        // Store username and password into variables
        String username = usernameField.getText();
        String password = passwordField.getText();

        LoginController userAuthentication = new LoginController();
        if (userAuthentication.isValidCredentials(username, password)) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/c195final/MainScreen.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            MainScreenController controller = loader.getController();
            // controller.sendProduct(productTableView.getSelectionModel().getSelectedIndex(),productTableView.getSelectionModel().getSelectedItem());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    @FXML
    public void loginButtonAction(ActionEvent event) throws IOException {

        if (userAuthentication.isValidCredentials(username, password)) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/c195final/MainScreen.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            MainScreenController controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
