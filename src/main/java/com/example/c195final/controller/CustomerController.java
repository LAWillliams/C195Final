package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import com.example.c195final.model.Customer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.util.Callback;

/**
 * @author lukea
 * */
public class CustomerController implements Initializable {

    public ObservableList<ObservableList> data;

    public TableView tableview;
    @FXML
    public TextField customerDeleteField;
    @FXML
    public TextField customerUpdateField;

    /**
     * Takes the user back to the main screen
     * @param event takes user input in the form of a mouse click
     * @throws IOException handles errors
     * */
    @FXML
    public void customerBackButtonAction(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/c195final/MainScreen.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        MainScreenController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

    /**
     * This defines a method to query the selected row and delete it from the database
     * */
    public static int customerDelete(int customerID) throws SQLException {
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,customerID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    public void customerDeleteAction(ActionEvent event) throws SQLException {
        String customerID = customerDeleteField.getText();
        customerDelete(Integer.parseInt(customerID));
        tableview.refresh();
    }

    public static Customer getCustomerByID(int customerID) throws SQLException {
        String sql = "SELECT * FROM customers WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int customerId = rs.getInt("Customer_ID");
            String name = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            String createDate = rs.getString("Create_Date");
            String createdBy = rs.getString("Created_By");
            String lastUpdate = rs.getString("Last_Update");
            String lastUpdatedBy = rs.getString("Last_Updated_By");
            String postalCode = rs.getString("Postal_Code");
            String phone = rs.getString("Phone");
            int divisionId = rs.getInt("Division_ID");

            // Create a new Customer object with the retrieved data
            Customer customer = new Customer(customerId, name, address, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdatedBy, divisionId);
            return customer;
        }

        // If no customer record was found with the given ID, return null
        return null;
    }

    public void customerUpdateAction(ActionEvent event) throws IOException, SQLException {
        String customerID = customerUpdateField.getText().trim(); // Trim to remove leading/trailing spaces

        // Check if the customerID is not empty or null
        if (!customerID.isEmpty()) {
            int customerIdInt = Integer.parseInt(customerID);

            // Retrieve the customer record using the provided customerID
            Customer customer = getCustomerByID(customerIdInt);

            if (customer != null) {
                // Load the new FXML screen
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/example/c195final/UpdateCustomer.fxml"));
                Parent parent = loader.load();
                Scene scene = new Scene(parent);

                // Pass the retrieved customer information to the controller of the new FXML screen
                UpdateCustomerController controller = loader.getController();
                controller.setCustomer(customer); // Assuming you have a setter in UpdateCustomerController

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                // Handle the case when no customer record is found with the provided ID
                // Display an error message or take appropriate action
            }
        } else {
            // Handle the case when the customerID field is empty or null
            // Display an error message or take appropriate action
        }
    }


    public void customerAddAction(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/c195final/CustomerCreate.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        CustomerCreateController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public static final String Customer_Query = "SELECT customers.Customer_ID,customers.Customer_Name,customers.Address,customers.Postal_Code,customers.Phone,customers.Division_ID, first_level_divisions.Division, countries.Country from customers LEFT JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID LEFT JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID";

    /**
     * This method queries the database using a predefined query, which is stored into a variable. After selecting the data from the query it stores it in a local variable named data. Then it builds the columns and rows and puts the data into the table view
     */
    public void buildData() {
        data = FXCollections.observableArrayList();
        try {
            //opens connection and executes query
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            System.out.println(connection.isClosed());;
            PreparedStatement statement = connection.prepareStatement(Customer_Query);
            ResultSet rs = statement.executeQuery();

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                tableview.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);
            }

            tableview.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        buildData();
        tableview = new TableView();

    }
}

