package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import com.example.c195final.model.Customer;
import com.example.c195final.model.DivisionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UpdateCustomerController implements Initializable {

    @FXML public TextField Customer_ID;
    @FXML public TextField Customer_Name;
    @FXML public TextField Address;
    @FXML public TextField Postal_Code;
    @FXML public TextField Phone;
    @FXML public ComboBox<DivisionItem> divisionComboBox;
    @FXML public ComboBox<String> countryComboBox;

    /**
     * Handles the action to navigate back to the Customer screen.
     *
     * @param event The event triggering the action.
     * @throws IOException If an I/O error occurs.
     */
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

    public void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Customer saved successfully.");
        alert.setContentText("The customer data has been saved successfully.");
        alert.showAndWait();
    }

    public void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error occurred while saving customer.");
        alert.setContentText(errorMessage);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Set minimum height to display full error message
        alert.showAndWait();
    }

    /**
     * Retrieves the division name based on the provided division ID.
     *
     * @param divisionId The ID of the division to find the name for.
     * @return The division name associated with the ID, or an empty string if not found.
     */
    public String fetchDivisionNameById(int divisionId) {
        String divisionName = ""; // Default value
        try {
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT Division FROM first_level_divisions WHERE Division_ID = ?");
            statement.setInt(1, divisionId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                divisionName = rs.getString("Division");
            }
            JDBC.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception appropriately
        }
        return divisionName;
    }

    /**
     * Sets the fields in the user interface with values from the provided Customer object.
     *
     * @param customer The Customer object containing the data to be displayed in the user interface.
     */
    public void setCustomer(Customer customer) {
        Customer_ID.setText(String.valueOf(customer.getCustomer_ID()));
        Customer_Name.setText(customer.getCustomer_Name());
        Address.setText(customer.getAddress());
        Postal_Code.setText(String.valueOf(customer.getPostal_Code()));
        Phone.setText(String.valueOf(customer.getPhone()));

        int customerDivisionId = customer.getDivision_ID();
        String customerDivisionName = fetchDivisionNameById(customerDivisionId);
        String customerCountry = fetchCountryByDivision(customerDivisionId);

        // Set the country and its divisions before attempting to set the division.
        countryComboBox.setValue(customerCountry);

        // Fetch and set divisions based on country
        try {
            int countryId = getCountryIdByName(customerCountry);
            ObservableList<DivisionItem> divisions = FXCollections.observableArrayList();
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT Division_ID, Division FROM first_level_divisions WHERE Country_ID = ?");
            statement.setInt(1, countryId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int divisionId = rs.getInt("Division_ID");
                String division = rs.getString("Division");
                divisions.add(new DivisionItem(divisionId, division));
            }
            divisionComboBox.setItems(divisions);
            JDBC.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception appropriately
        }

        if (!customerDivisionName.isEmpty()) {
            ObservableList<DivisionItem> divisionItems = divisionComboBox.getItems();
            for (DivisionItem divisionItem : divisionItems) {
                if (divisionItem.getDivisionName().equals(customerDivisionName)) {
                    divisionComboBox.setValue(divisionItem);
                    break;
                }
            }
        }

        countryComboBox.setValue(customerCountry);

    }

    /**
     * Fetches and sets the country in the UI based on the provided division ID.
     *
     * @param divisionId The ID of the division to fetch the associated country for.
     * @return The name of the country associated with the division.
     */
    public String fetchCountryByDivision(int divisionId) {
        String country = ""; // Default value
        try {
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT Country FROM countries WHERE Country_ID = ?");
            statement.setInt(1, getCountryIdByDivision(divisionId)); // Assuming you have a method to retrieve the Country_ID from the Division_ID
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                country = rs.getString("Country");
            }
            JDBC.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception appropriately
        }
        return country;
    }

    /**
     * Retrieves the country ID associated with the provided division ID.
     *
     * @param divisionId The ID of the division to find the associated country ID for.
     * @return The country ID associated with the division, or -1 if not found.
     * @throws SQLException If a database access error occurs.
     */
    public int getCountryIdByDivision(int divisionId) throws SQLException {
        int countryId = -1; // Default value if not found or error occurs

        JDBC.openConnection();
        String sql = "SELECT Country_ID FROM first_level_divisions WHERE Division_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, divisionId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            countryId = rs.getInt("Country_ID");
        }

        return countryId;
    }

    /**
     * Handles the action to update and save customer information.
     *
     * @param event The event triggering the action.
     * @return The number of rows affected by the update operation.
     * @throws SQLException If a database access error occurs.
     * @throws IOException  If an I/O error occurs.
     */
    public int updateCustomerSaveAction(ActionEvent event) throws SQLException, IOException {
        JDBC.openConnection();
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, Customer_Name.getText());
        ps.setString(2, Address.getText());
        ps.setString(3, Postal_Code.getText());
        ps.setString(4, Phone.getText());

        DivisionItem selectedDivision = divisionComboBox.getValue();
        if (selectedDivision != null) {
            ps.setInt(5, selectedDivision.getDivisionId()); // Assuming DivisionItem has a method to get Division ID
        } else {
            // Handle the case when no division is selected
            // For example: ps.setNull(9, Types.INTEGER);
        }

        // Set other parameters...

        int customerID = Integer.parseInt(Customer_ID.getText());
        ps.setInt(6, customerID);
        int rowsAffected = ps.executeUpdate();

        if (rowsAffected == 1) {
            showSuccessAlert();
            updateCustomerBackButtonAction(event); // Go back to the Customer.fxml screen
        } else {
            showErrorAlert("Failed to save customer data.");
        }

        return rowsAffected;
    }

    /**
     * Retrieves the country ID based on the provided country name.
     *
     * @param countryName The name of the country to find the ID for.
     * @return The country ID, or -1 if not found.
     * @throws SQLException If a database access error occurs.
     */
    public int getCountryIdByName(String countryName) throws SQLException {
        int countryId = -1; // Default value if not found or error occurs

        try {
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT Country_ID FROM countries WHERE Country = ?");
            statement.setString(1, countryName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                countryId = rs.getInt("Country_ID");
            }
            JDBC.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception appropriately
        }

        return countryId;
    }


    /**
     * Initializes the controller, populating combo boxes and adding listeners.
     *
     * @param url            The URL to initialize the controller.
     * @param resourceBundle The resource bundle associated with the controller.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        JDBC.openConnection();
        ObservableList<String> countries = FXCollections.observableArrayList();

        try {
            Connection connection = JDBC.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT Country FROM countries");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                countries.add(rs.getString("Country"));
            }
            countryComboBox.setItems(countries);
            JDBC.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception appropriately
        }

        countryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    int countryId = getCountryIdByName(newValue); // Fetch the Country_ID based on the selected country name
                    ObservableList<DivisionItem> divisions = FXCollections.observableArrayList();

                    try {
                        JDBC.openConnection();
                        Connection connection = JDBC.getConnection();
                        PreparedStatement statement = connection.prepareStatement("SELECT Division_ID, Division FROM first_level_divisions WHERE Country_ID = ?");
                        statement.setInt(1, countryId); // Use the retrieved Country_ID
                        ResultSet rs = statement.executeQuery();
                        while (rs.next()) {
                            int divisionId = rs.getInt("Division_ID");
                            String division = rs.getString("Division");
                            divisions.add(new DivisionItem(divisionId, division));
                        }
                        divisionComboBox.setItems(divisions);
                        JDBC.closeConnection();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle exception appropriately
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Handle exception appropriately
                }
            } else {
                divisionComboBox.setItems(FXCollections.emptyObservableList());
            }
        });

        // ... (other setup)
    }


}
