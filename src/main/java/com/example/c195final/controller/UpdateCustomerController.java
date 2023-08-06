package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import com.example.c195final.model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UpdateCustomerController implements Initializable {

    @FXML
    public TextField Customer_ID;
    @FXML
    public TextField Customer_Name;
    @FXML
    public TextField Address;
    @FXML
    public TextField Postal_Code;
    @FXML
    public TextField Phone;
    @FXML
    public TextField Create_Date;
    @FXML
    public TextField Created_By;
    @FXML
    public TextField Last_Update;
    @FXML
    public TextField Last_Updated_By;
    @FXML
    public TextField Division_ID;
    public void updateCustomerBackButtonAction(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/c195final/Customer.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        CustomerController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void setCustomer(Customer customer) {
        Customer_ID.setText(String.valueOf(customer.getCustomer_ID()));
        Customer_Name.setText(customer.getCustomer_Name());
        Address.setText(customer.getAddress());
        Postal_Code.setText(String.valueOf(customer.getPostal_Code()));
        Phone.setText(String.valueOf(customer.getPhone()));
        Create_Date.setText(customer.getCreate_Date());
        Created_By.setText(customer.getCreated_By());
        Last_Update.setText(customer.getLast_Update());
        Last_Updated_By.setText(customer.getLast_Updated_By());
        Division_ID.setText(String.valueOf(customer.getDivision_ID()));


        // Set other relevant fields in the controller based on the Customer object
    }

    public int updateCustomerSaveAction(ActionEvent event) throws SQLException {

        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(Customer_Name));
        ps.setString(2, String.valueOf(Address));
        ps.setString(3, String.valueOf(Postal_Code));
        ps.setString(4, String.valueOf(Phone));
        ps.setString(5, String.valueOf(Create_Date));
        ps.setString(6, String.valueOf(Created_By));
        ps.setString(7, String.valueOf(Last_Update));
        ps.setString(8, String.valueOf(Last_Updated_By));
        ps.setString(9, String.valueOf(Division_ID));
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
