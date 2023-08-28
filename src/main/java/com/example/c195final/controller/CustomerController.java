package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import com.example.c195final.model.Customer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
     * Handles the action when the "Back" button is clicked.
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If an I/O exception occurs.
     */
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
     * Deletes a customer record from the database based on the provided customer ID.
     *
     * @param customerID The ID of the customer to be deleted.
     * @return The number of rows affected by the deletion operation.
     * @throws SQLException If a database access error occurs.
     */
    public static int customerDelete(int customerID) throws SQLException {
        // Check for associated appointments
        if (checkForAppointments(customerID)) {
            // Delete associated appointments
            deleteAppointmentsForCustomer(customerID);
        }

        // Now, delete the customer
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Deletes all appointments associated with a given customer.
     *
     * @param customerID The ID of the customer whose appointments need to be deleted.
     * @throws SQLException If a database access error occurs.
     */
    public static void deleteAppointmentsForCustomer(int customerID) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        ps.executeUpdate();
    }

    /**
     * Handles the action to delete a customer and their associated appointments.
     *
     * @param event The event triggering the action.
     * @throws SQLException If a database access error occurs.
     */
    public void customerDeleteAction(ActionEvent event) throws SQLException {
        ObservableList<String> selectedRow = (ObservableList<String>) tableview.getSelectionModel().getSelectedItem();

        if (selectedRow != null) {
            String customerID = selectedRow.get(0); // Assuming Customer_ID is the first column
            int deletedCustomerID = Integer.parseInt(customerID);

            // Check if there are associated appointments
            boolean hasAppointments = checkForAppointments(deletedCustomerID);

            // Create and configure the confirmation alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Customer");
            alert.setHeaderText("Delete Customer and Associated Appointments");

            if (hasAppointments) {
                alert.setContentText("Deleting this customer will also delete all associated appointments. Continue?");
            } else {
                alert.setContentText("Are you sure you want to delete this customer?");
            }

            // If the user confirms, proceed with deletion
            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (result == ButtonType.OK) {
                int rowsAffected = customerDelete(deletedCustomerID);
                if (rowsAffected > 0) {
                    // Remove the selected customer row from the data list
                    data.remove(selectedRow);

                    // Refresh the TableView
                    tableview.refresh();
                }
            }
        } else {
            // Handle no row selected
        }
    }



    /**
     * Checks if there are any appointments associated with a customer.
     *
     * @param customerID The ID of the customer to check for appointments.
     * @return True if there are appointments associated with the customer, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public static boolean checkForAppointments(int customerID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }

    /**
     * Retrieves customer information by their ID.
     *
     * @param customerID The ID of the customer to retrieve.
     * @return A Customer object containing the retrieved customer information, or null if not found.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Handles the action when the "Update Customer" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If an I/O exception occurs.
     * @throws SQLException If a database access error occurs.
     */
    public void customerUpdateAction(ActionEvent event) throws IOException, SQLException {
        ObservableList<String> selectedRow = (ObservableList<String>) tableview.getSelectionModel().getSelectedItem();

        if (selectedRow != null) {
            String customerID = selectedRow.get(0); // Assuming Customer_ID is the first column
            int customerIdInt = Integer.parseInt(customerID);

            Customer customer = getCustomerByID(customerIdInt);

            if (customer != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195final/UpdateCustomer.fxml"));
                Parent parent = loader.load();
                Scene scene = new Scene(parent);

                UpdateCustomerController controller = loader.getController();
                controller.setCustomer(customer);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                // Handle no customer record found
            }
        } else {
            // Handle no row selected
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
    }
}

