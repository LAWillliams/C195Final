package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CustomerCreateController implements Initializable {

    @FXML public TextField Customer_ID;
    @FXML public TextField Customer_Name;
    @FXML public TextField Address;
    @FXML public TextField Postal_Code;
    @FXML public TextField Phone;
    @FXML public TextField Create_Date;
    @FXML public TextField Created_By;
    @FXML public TextField Last_Update;
    @FXML public TextField Last_Updated_By;
    @FXML public TextField Division_ID;

    public void customerCreateBackButtonAction(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/c195final/Customer.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        CustomerController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

//    public static int insert(String countryName, int countryId) throws SQLException {
//        String sql = "INSERT INTO COUNTRIES (Country, Country_ID) VALUES(?, ?)";
//        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
//        ps.setString(1,countryName);
//        ps.setInt(2,countryId);
//        int rowsAffected = ps.executeUpdate();
//        return rowsAffected;
//    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Customer saved successfully.");
        alert.setContentText("The customer data has been saved successfully.");
        alert.showAndWait();
    }

    private void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error occurred while saving customer.");
        alert.setContentText(errorMessage);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Set minimum height to display full error message
        alert.showAndWait();
    }
    public int customerSaveAction(ActionEvent event) throws SQLException, IOException {
        String sql = "INSERT INTO customers (Customer_Name,Address,Postal_Code,Phone,Division_ID)VALUES(?,?,?,?,?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, Customer_Name.getText());
        ps.setString(2, Address.getText());
        ps.setString(3, Postal_Code.getText());
        ps.setString(4, Phone.getText());
        int divisionID = Integer.parseInt(Division_ID.getText());
        ps.setInt(5, divisionID);
        int rowsAffected = ps.executeUpdate();

        if (rowsAffected == 1) {
            showSuccessAlert();
            customerCreateBackButtonAction(event); // Go back to the Customer.fxml screen
        } else {
            showErrorAlert("Failed to save customer data.");
        };
        return rowsAffected;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
