package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
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

public class CustomerCreateController implements Initializable {

    @FXML public TextField Customer_ID;
    @FXML public TextField Customer_Name;
    @FXML public TextField Address;
    @FXML public TextField Postal_Code;
    @FXML public TextField Phone;
    @FXML public ComboBox<DivisionItem> divisionComboBox;
    @FXML public ComboBox<String> countryComboBox;


    /**
     * Handles the action when the "Back" button is clicked.
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If an I/O exception occurs.
     */
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

    /**
     * Displays a success alert.
     * Shows an information alert indicating that the customer data has been saved successfully.
     */
    public void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Customer saved successfully.");
        alert.setContentText("The customer data has been saved successfully.");
        alert.showAndWait();
    }

    /**
     * Displays an error alert.
     * Shows an error alert indicating that an error occurred while saving the customer data.
     * @param errorMessage The error message to be displayed in the alert.
     */
    public void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error occurred while saving customer.");
        alert.setContentText(errorMessage);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Set minimum height to display full error message
        alert.showAndWait();
    }
    public int customerSaveAction(ActionEvent event) throws SQLException, IOException {
        JDBC.openConnection();
        String sql = "INSERT INTO customers (Customer_Name,Address,Postal_Code,Phone,Division_ID)VALUES(?,?,?,?,?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, Customer_Name.getText());
        ps.setString(2, Address.getText());
        ps.setString(3, Postal_Code.getText());
        ps.setString(4, Phone.getText());
        DivisionItem selectedDivision = divisionComboBox.getValue();
        if (selectedDivision != null) {
            ps.setInt(5, selectedDivision.getDivisionId());
        } else {
            // Handle the case when no division is selected
        }

        int rowsAffected = ps.executeUpdate();

        if (rowsAffected == 1) {
            showSuccessAlert();
            customerCreateBackButtonAction(event);
        } else {
            showErrorAlert("Failed to save customer data.");
        }
        return rowsAffected;
    }

    /**
     * Initializes the controller with data from the database.
     * Populates the countryComboBox and manages divisionComboBox based on selected country.
     *
     * @param url The URL of the location used to resolve the object.
     * @param resourceBundle The ResourceBundle to be used for localization.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        JDBC.openConnection();
        ObservableList<String> countries = FXCollections.observableArrayList();
        try {
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT Country FROM countries");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                countries.add(rs.getString("Country"));
            }

            // Populate countryComboBox
            countryComboBox.setItems(countries);

            JDBC.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception appropriately
        }

        countryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<DivisionItem> divisions = FXCollections.observableArrayList();
                try {
                    JDBC.openConnection();
                    Connection connection = JDBC.getConnection();
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT Division_ID, Division FROM first_level_divisions WHERE Country_ID = (SELECT Country_ID FROM countries WHERE Country = ?)"
                    );
                    statement.setString(1, newValue);
                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        int divisionId = rs.getInt("Division_ID");
                        String divisionName = rs.getString("Division");
                        divisions.add(new DivisionItem(divisionId, divisionName));
                    }

                    divisionComboBox.setItems(divisions);

                    JDBC.closeConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle exception appropriately
                }
            } else {
                divisionComboBox.setItems(FXCollections.emptyObservableList());
            }
        });



    }
}
