package com.example.c195final.controller;

import com.example.c195final.controller.AppointmentViewController;
import com.example.c195final.helper.JDBC;
import com.example.c195final.model.Appointment;
import com.example.c195final.model.Contact;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class UpdateAppointmentController implements Initializable {

    @FXML
    public TextField Appointment_ID;

    @FXML
    public TextField Title;

    @FXML
    public TextField Type;

    @FXML
    public TextField Description;

    @FXML
    public TextField Location;

    @FXML
    public DatePicker startDatePicker;
    @FXML
    public ComboBox<Integer> startHourComboBox;
    @FXML
    public ComboBox<Integer> startMinuteComboBox;
    @FXML
    public DatePicker endDatePicker;
    @FXML
    public ComboBox<Integer> endHourComboBox;
    @FXML
    public ComboBox<Integer> endMinuteComboBox;
    @FXML
    public ComboBox<String> contactComboBox;
    @FXML
    public ComboBox<Integer> customerComboBox;
    @FXML
    public ComboBox<Integer> userComboBox;


    @FXML
    public void appointmentUpdateBackButton(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/c195final/AppointmentView.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        AppointmentViewController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

    public void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Appointment saved successfully.");
        alert.setContentText("The appointment data has been saved successfully.");
        alert.showAndWait();
    }

    public void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error occurred while saving appointment.");
        alert.setContentText(errorMessage);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Set minimum height to display full error message
        alert.showAndWait();
    }

    /**
     * Retrieves the contact ID based on the contact name.
     *
     * @param contactName The name of the contact to find the ID for.
     * @return The contact ID, or -1 if not found.
     * @throws SQLException If a database access error occurs.
     */
    public int getContactIdByName(String contactName) throws SQLException {
        JDBC.openConnection();
        String sql = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, contactName);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("Contact_ID");
        }

        // Return a default value or handle the case when contact is not found
        return -1;
    }

    /**
     * Sets the fields in the UI with appointment data.
     *
     * @param appointment The appointment to display in the UI.
     * @throws SQLException If a database access error occurs.
     */
    public void setAppointment(Appointment appointment) throws SQLException {
        JDBC.openConnection();
        Appointment_ID.setText(String.valueOf(appointment.getAppointmentId()));
        Title.setText(appointment.getTitle());
        Description.setText(appointment.getDescription());
        Location.setText(appointment.getLocation());
        populateContactComboBox();

        // Select the appropriate contact
        int selectedContact = getContactIdByName(String.valueOf(Integer.parseInt(appointment.getContactId())));
        String selectedContactName = String.valueOf(selectedContact);
        contactComboBox.getSelectionModel().select(selectedContactName);


        Type.setText(appointment.getType());

        // Convert and set start date and time components
        LocalDateTime startDateTime = appointment.getStart();
        startDatePicker.setValue(startDateTime.toLocalDate());
        startHourComboBox.getSelectionModel().select(startDateTime.getHour());
        startMinuteComboBox.getSelectionModel().select(startDateTime.getMinute());

        // Convert and set end date and time components
        LocalDateTime endDateTime = appointment.getEnd();
        endDatePicker.setValue(endDateTime.toLocalDate());
        endHourComboBox.getSelectionModel().select(endDateTime.getHour());
        endMinuteComboBox.getSelectionModel().select(endDateTime.getMinute());

        customerComboBox.getSelectionModel().select(Integer.parseInt(appointment.getCustomerId()));
        userComboBox.getSelectionModel().select(appointment.getUserId());

    }

    /**
     * Populates the contact combo box with contact names retrieved from the database.
     */
    public void populateContactComboBox() {
        try {
            JDBC.openConnection();
            String sql = "SELECT * FROM contacts";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            ObservableList<String> contactList = FXCollections.observableArrayList();

            while (rs.next()) {
                int contactId = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");
                Contact contact = new Contact(contactId, contactName);
                contactList.add(contact.getContactName());
            }

            contactComboBox.setItems(contactList);

            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the save action for updating an appointment.
     *
     * @param event The ActionEvent triggering the update and save action.
     * @return The number of rows affected, or 0 if the operation failed.
     * @throws SQLException If a database access error occurs.
     * @throws IOException If an I/O error occurs.
     */
    public int appointmentUpdateSaveAction(ActionEvent event) throws SQLException, IOException {
        JDBC.openConnection();
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, Contact_ID = ?, User_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        try {
            ps.setInt(10, Integer.parseInt(Appointment_ID.getText()));
            ps.setInt(7, customerComboBox.getSelectionModel().getSelectedItem());
            String selectedContactName = contactComboBox.getSelectionModel().getSelectedItem();
            int selectedContactId = getContactIdByName(selectedContactName);
            ps.setInt(8, selectedContactId);
            ps.setInt(9, userComboBox.getSelectionModel().getSelectedItem());
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid integer input. Please provide valid integer values.");
            return 0; // Indicate that the operation failed
        }

        ps.setString(1, Title.getText());
        ps.setString(2, Description.getText());
        ps.setString(3, Location.getText());
        ps.setString(4, Type.getText());
        ps.setString(5, startDatePicker.getValue() + " " + startHourComboBox.getValue() + ":" + startMinuteComboBox.getValue() + ":00");
        ps.setString(6, endDatePicker.getValue() + " " + endHourComboBox.getValue() + ":" + endMinuteComboBox.getValue() + ":00");

        int rowsAffected = ps.executeUpdate();

        if (rowsAffected == 1) {
            showSuccessAlert();
            appointmentUpdateBackButton(event); // Go back to the Customer.fxml screen
        } else {
            showErrorAlert("Failed to update appointment data.");
        }
        return rowsAffected;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
