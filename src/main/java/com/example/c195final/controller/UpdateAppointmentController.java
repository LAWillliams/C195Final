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
import java.sql.Timestamp;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    /**
     * Handles the action when the "Back" button is clicked.
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If an I/O exception occurs.
     */
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
        populateAppointmentFields(appointment);
        populateComboBoxes(appointment);
    }

    public void populateAppointmentFields(Appointment appointment) {
        Appointment_ID.setText(String.valueOf(appointment.getAppointmentId()));
        Title.setText(appointment.getTitle());
        Description.setText(appointment.getDescription());
        Location.setText(appointment.getLocation());
        Type.setText(appointment.getType());

        // Convert UTC time to local time
        LocalDateTime startDateTimeUTC = appointment.getStart();
        LocalDateTime startDateTimeLocal = startDateTimeUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        startDatePicker.setValue(startDateTimeLocal.toLocalDate());
        startHourComboBox.setValue(startDateTimeLocal.getHour());
        startMinuteComboBox.setValue(startDateTimeLocal.getMinute());

        LocalDateTime endDateTimeUTC = appointment.getEnd();
        LocalDateTime endDateTimeLocal = endDateTimeUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        endDatePicker.setValue(endDateTimeLocal.toLocalDate());
        endHourComboBox.setValue(endDateTimeLocal.getHour());
        endMinuteComboBox.setValue(endDateTimeLocal.getMinute());

        customerComboBox.setValue(Integer.parseInt(appointment.getCustomerId()));
        userComboBox.setValue(appointment.getUserId());
    }


    public void populateComboBoxes(Appointment appointment) throws SQLException {
        populateContactComboBox(appointment);
        // Populate other combo boxes as needed
    }

    /**
     * Populates the contact combo box with contact names retrieved from the database.
     */
    public void populateContactComboBox(Appointment appointment) throws SQLException {
        int selectedContactId = Integer.parseInt(appointment.getContactId());
        String selectedContactName = getContactNameById(selectedContactId);
        contactComboBox.getSelectionModel().select(selectedContactName);
    }

    /**
     * Retrieves the contact name based on the provided contact ID.
     *
     * @param contactId The ID of the contact to find the name for.
     * @return The contact name associated with the ID, or an empty string if not found.
     * @throws SQLException If a database access error occurs.
     */
    public String getContactNameById(int contactId) throws SQLException {
        JDBC.openConnection();
        String sql = "SELECT Contact_Name FROM contacts WHERE Contact_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, contactId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("Contact_Name");
        }
        return ""; // Handle not found case
    }

    /**
     * Checks if the selected appointment's time falls within business hours.
     *
     * @return True if the appointment time is within business hours (8 AM - 10 PM), false otherwise.
     */
    public boolean isBusinessHours() {
        int startHour = startHourComboBox.getSelectionModel().getSelectedItem();
        int endHour = endHourComboBox.getSelectionModel().getSelectedItem();
        int startMinute = startMinuteComboBox.getSelectionModel().getSelectedItem();
        int endMinute = endMinuteComboBox.getSelectionModel().getSelectedItem();

        LocalTime openingTimeET = LocalTime.of(8, 0);
        LocalTime closingTimeET = LocalTime.of(22, 0);

        ZoneId zoneId = ZoneId.of("America/New_York");

        ZonedDateTime startDateTimeET = ZonedDateTime.of(LocalDate.now(), LocalTime.of(startHour, startMinute), ZoneId.systemDefault())
                .withZoneSameInstant(zoneId);
        ZonedDateTime endDateTimeET = ZonedDateTime.of(LocalDate.now(), LocalTime.of(endHour, endMinute), ZoneId.systemDefault())
                .withZoneSameInstant(zoneId);

        LocalTime startET = startDateTimeET.toLocalTime();
        LocalTime endET = endDateTimeET.toLocalTime();

        if (startET.isBefore(openingTimeET) || endET.isAfter(closingTimeET)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if there is an overlapping appointment for the selected customer.
     * @param appointmentId The ID of the appointment to exclude from the check.
     * @return true if overlapping appointment found, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean isOverlappingAppointment(int appointmentId) throws SQLException {
        int selectedCustomerId = customerComboBox.getSelectionModel().getSelectedItem();
        LocalDate startDate = startDatePicker.getValue();
        int startHour = startHourComboBox.getSelectionModel().getSelectedItem();
        int startMinute = startMinuteComboBox.getSelectionModel().getSelectedItem();
        LocalDateTime newStartTime = LocalDateTime.of(startDate, LocalTime.of(startHour, startMinute));
        LocalDate endDate = endDatePicker.getValue();
        int endHour = endHourComboBox.getSelectionModel().getSelectedItem();
        int endMinute = endMinuteComboBox.getSelectionModel().getSelectedItem();
        LocalDateTime newEndTime = LocalDateTime.of(endDate, LocalTime.of(endHour, endMinute));

        String sql = "SELECT * FROM appointments WHERE Customer_ID = ? AND Appointment_ID != ? " +
                "AND ((Start <= ? AND End >= ?) OR (Start <= ? AND End >= ?))";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, selectedCustomerId);
        ps.setInt(2, appointmentId);
        ps.setTimestamp(3, Timestamp.valueOf(newEndTime));
        ps.setTimestamp(4, Timestamp.valueOf(newStartTime));
        ps.setTimestamp(5, Timestamp.valueOf(newEndTime));
        ps.setTimestamp(6, Timestamp.valueOf(newStartTime));

        ResultSet rs = ps.executeQuery();
        return rs.next(); // Returns true if overlapping appointment found
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
        if (!isBusinessHours()) {
            showErrorAlert("Appointments must be scheduled during business hours (8 AM - 10 PM).");
            return 0; // Indicate that the operation failed
        }

        int appointmentId = Integer.parseInt(Appointment_ID.getText());

        // Check if there's an overlapping appointment (excluding the current appointment)
        if (isOverlappingAppointment(appointmentId)) {
            showErrorAlert("There is an overlapping appointment for the selected customer.");
            return 0; // Indicate that the operation failed
        }

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

        // Convert local start and end times to UTC Instant
        Instant startInstant = LocalDateTime.of(startDatePicker.getValue(),
                LocalTime.of(startHourComboBox.getValue(), startMinuteComboBox.getValue()))
                .atZone(ZoneId.systemDefault()).toInstant();
        Instant endInstant = LocalDateTime.of(endDatePicker.getValue(),
                LocalTime.of(endHourComboBox.getValue(), endMinuteComboBox.getValue()))
                .atZone(ZoneId.systemDefault()).toInstant();

        // Set UTC times in the prepared statement
        ps.setTimestamp(5, Timestamp.from(startInstant));
        ps.setTimestamp(6, Timestamp.from(endInstant));

        int rowsAffected = ps.executeUpdate();

        if (rowsAffected == 1) {
            showSuccessAlert();
            appointmentUpdateBackButton(event); // Go back to the Customer.fxml screen
        } else {
            showErrorAlert("Failed to update appointment data.");
        }

        return rowsAffected;
    }

    /**
     * Populates the time combo boxes with hours and minutes.
     * Hours range from 0 to 23, and minutes range from 0 to 59.
     */
    public void populateTimeComboBoxes() {
        List<Integer> hours = IntStream.rangeClosed(0, 23).boxed().collect(Collectors.toList());
        List<Integer> minutes = IntStream.rangeClosed(0, 59).boxed().collect(Collectors.toList());

        startHourComboBox.setItems(FXCollections.observableArrayList(hours));
        startMinuteComboBox.setItems(FXCollections.observableArrayList(minutes));
        endHourComboBox.setItems(FXCollections.observableArrayList(hours));
        endMinuteComboBox.setItems(FXCollections.observableArrayList(minutes));
    }

    /**
     * Populates the contact combo box with contact names retrieved from the database.
     */
    public void populateContactComboBox() {
        try {
            JDBC.openConnection();
            String sql = "SELECT Contact_Name FROM contacts";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<String> contactNames = new ArrayList<>();
            while (rs.next()) {
                contactNames.add(rs.getString("Contact_Name"));
            }

            contactComboBox.setItems(FXCollections.observableArrayList(contactNames));
            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the customer combo box with customer IDs retrieved from the database.
     */
    public void populateCustomerComboBox() {
        try {
            JDBC.openConnection();
            String sql = "SELECT Customer_ID FROM customers";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<Integer> customerIds = new ArrayList<>();
            while (rs.next()) {
                customerIds.add(rs.getInt("Customer_ID"));
            }

            customerComboBox.setItems(FXCollections.observableArrayList(customerIds));
            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the user combo box with user IDs retrieved from the database.
     */
    public void populateUserComboBox() {
        try {
            JDBC.openConnection();
            String sql = "SELECT User_ID FROM users";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List<Integer> userIds = new ArrayList<>();
            while (rs.next()) {
                userIds.add(rs.getInt("User_ID"));        }
            userComboBox.setItems(FXCollections.observableArrayList(userIds));
            JDBC.closeConnection();    }
        catch (SQLException e) {
            e.printStackTrace();    }
    }

    /**
     * Initializes the class with necessary resources and UI elements.
     * This method is called automatically when the FXML file is loaded.
     * @param url The URL for the object(s).
     * @param resourceBundle The resource bundle to be used for localization.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateContactComboBox();
        populateCustomerComboBox();
        populateUserComboBox();
        populateTimeComboBoxes();
    }
}
