package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import javafx.collections.FXCollections;
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

import java.sql.*;
import java.time.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AppointmentCreateController implements Initializable {

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
    public void appointmentCreateBackButton(ActionEvent event) throws IOException {

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


    public boolean isOverlappingAppointment() throws SQLException {
        int selectedCustomerId = customerComboBox.getSelectionModel().getSelectedItem();
        LocalDate startDate = startDatePicker.getValue();
        int startHour = startHourComboBox.getSelectionModel().getSelectedItem();
        int startMinute = startMinuteComboBox.getSelectionModel().getSelectedItem();
        LocalDateTime newStartTime = LocalDateTime.of(startDate, LocalTime.of(startHour, startMinute));

        LocalDate endDate = endDatePicker.getValue();
        int endHour = endHourComboBox.getSelectionModel().getSelectedItem();
        int endMinute = endMinuteComboBox.getSelectionModel().getSelectedItem();
        LocalDateTime newEndTime = LocalDateTime.of(endDate, LocalTime.of(endHour, endMinute));

        String sql = "SELECT * FROM appointments WHERE Customer_ID = ? " +
                "AND ((Start <= ? AND End >= ?) OR (Start <= ? AND End >= ?))";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, selectedCustomerId);
        ps.setTimestamp(2, Timestamp.valueOf(newEndTime));
        ps.setTimestamp(3, Timestamp.valueOf(newStartTime));
        ps.setTimestamp(4, Timestamp.valueOf(newEndTime));
        ps.setTimestamp(5, Timestamp.valueOf(newStartTime));

        ResultSet rs = ps.executeQuery();
        return rs.next(); // Returns true if overlapping appointment found
    }



    public int getContactIdByName(String contactName) throws SQLException {
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


    public int appointmentSaveAction(ActionEvent event) throws SQLException, IOException {
        JDBC.openConnection();
        try {
            JDBC.openConnection();
            int selectedCustomerId = customerComboBox.getSelectionModel().getSelectedItem();
            int selectedUserId = userComboBox.getSelectionModel().getSelectedItem();
            String selectedContact = contactComboBox.getSelectionModel().getSelectedItem();
            int contactId = getContactIdByName(selectedContact);
            // Get the values from UI elements
            String title = Title.getText();
            String description = Description.getText();
            String location = Location.getText();
            String type = Type.getText();
            LocalDate startDate = startDatePicker.getValue();
            int startHour = startHourComboBox.getSelectionModel().getSelectedItem();
            int startMinute = startMinuteComboBox.getSelectionModel().getSelectedItem();
            LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.of(startHour, startMinute));
            LocalDate endDate = endDatePicker.getValue();
            int endHour = endHourComboBox.getSelectionModel().getSelectedItem();
            int endMinute = endMinuteComboBox.getSelectionModel().getSelectedItem();
            LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.of(endHour, endMinute));

            // Check for business hours for both start and end hours
            if (!isBusinessHours()) {
                showErrorAlert("Appointments can only be scheduled between 8:00 a.m. and 10:00 p.m. ET, including weekends.");
                return 0;
            }

            // Check for overlapping appointments
            if (isOverlappingAppointment()) {
                showErrorAlert("Customer already has an overlapping appointment.");
                return 0;
            }

            String sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Customer_ID, Contact_ID, User_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            JDBC.openConnection();
            PreparedStatement ps = JDBC.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            // Set parameters
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setTimestamp(5, Timestamp.valueOf(startDateTime));
            ps.setTimestamp(6, Timestamp.valueOf(startDateTime));
            ps.setInt(7, selectedCustomerId);
            ps.setInt(8, contactId);
            ps.setInt(9, selectedUserId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                // Get the auto-generated Appointment_ID
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int appointmentId = generatedKeys.getInt(1);
                    showSuccessAlert();
                    appointmentCreateBackButton(event); // Go back to the AppointmentView.fxml screen
                }
            } else {
                showErrorAlert("Failed to save appointment data.");
            }
            return rowsAffected;
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid input. Please provide valid values.");
            return 0; // Indicate that the operation failed
        }
    }


    public void populateTimeComboBoxes() {
        List<Integer> hours = IntStream.rangeClosed(0, 23).boxed().collect(Collectors.toList());
        List<Integer> minutes = IntStream.rangeClosed(0, 59).boxed().collect(Collectors.toList());

        startHourComboBox.setItems(FXCollections.observableArrayList(hours));
        startMinuteComboBox.setItems(FXCollections.observableArrayList(minutes));
        endHourComboBox.setItems(FXCollections.observableArrayList(hours));
        endMinuteComboBox.setItems(FXCollections.observableArrayList(minutes));
    }

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

    public void populateUserComboBox() {
        try {
            JDBC.openConnection();
            String sql = "SELECT User_ID FROM users";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<Integer> userIds = new ArrayList<>();
            while (rs.next()) {
                userIds.add(rs.getInt("User_ID"));
            }

            userComboBox.setItems(FXCollections.observableArrayList(userIds));
            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate the contactComboBox with actual contact names from the database
        populateContactComboBox();
        populateCustomerComboBox();
        populateUserComboBox();
        populateTimeComboBoxes();
    }




}
