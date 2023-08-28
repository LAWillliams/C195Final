package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {


    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private TableView<ObservableList<String>> tableview;


    /**
     * Handles the action when the "Back" button is clicked.
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If an I/O exception occurs.
     */
    @FXML
    public void reportsBackButtonAction(ActionEvent event) throws IOException {
        loadScreen("/com/example/c195final/MainScreen.fxml", event);
    }

    public void loadScreen(String resource, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        MainScreenController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Generates a report of appointment types and their counts for each month.
     *
     * @param event The ActionEvent triggering the report generation.
     */
    @FXML
    public void generateAppointmentTypeAndMonthReport(ActionEvent event) {
        try {
            JDBC.openConnection();
            String sql = "SELECT MONTH(Start) AS Month, Type, COUNT(*) AS Count FROM appointments GROUP BY Month, Type";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            buildData(rs); // Utilize the buildData method to populate the TableView

            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void populateContactComboBox() {
        try {
            JDBC.openConnection();
            String sql = "SELECT Contact_Name FROM contacts";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            ObservableList<String> contactNames = FXCollections.observableArrayList();
            while (rs.next()) {
                contactNames.add(rs.getString("Contact_Name"));
            }

            contactComboBox.setItems(contactNames);

            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a report of contact schedules.
     *
     * @param event The ActionEvent triggering the report generation.
     */
    @FXML
    public void generateContactScheduleReport(ActionEvent event) {
        String selectedContact = contactComboBox.getValue();
        if (selectedContact == null || selectedContact.isEmpty()) {
            // Handle no contact selected
            return;
        }

        try {
            JDBC.openConnection();
            String sql = "SELECT a.Appointment_ID, a.Title, a.Type, a.Description, a.Start, a.End, a.Customer_ID " +
                    "FROM appointments a " +
                    "JOIN contacts c ON a.Contact_ID = c.Contact_ID " +
                    "WHERE c.Contact_Name = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, selectedContact);
            ResultSet rs = ps.executeQuery();

            buildData(rs); // Utilize the buildData method to populate the TableView

            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Generates a report of customer divisions.
     *
     * @param event The ActionEvent triggering the report generation.
     */
    @FXML
    public void generateCustomerDivisionReport(ActionEvent event) {
        try {
            JDBC.openConnection();
            String sql = "SELECT c.Customer_Name, d.Division FROM customers c " +
                    "JOIN first_level_divisions d ON c.Division_ID = d.Division_ID";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            buildData(rs); // Utilize the buildData method to populate the TableView

            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LocalDateTime convertToLocal(Instant instant) {
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public void buildData(ResultSet rs) throws SQLException {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        // Clear existing columns before adding new ones
        tableview.getColumns().clear();

        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
            final int j = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(rs.getMetaData().getColumnName(i + 1));
            col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
            tableview.getColumns().add(col);
            System.out.println("Column [" + i + "]");
        }

        while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                // Handle columns that represent times
                if ("Start".equals(rs.getMetaData().getColumnName(i)) || "End".equals(rs.getMetaData().getColumnName(i))) {
                    Instant utcInstant = rs.getTimestamp(i).toInstant();
                    LocalDateTime localTime = convertToLocal(utcInstant);
                    row.add(localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } else {
                    row.add(rs.getString(i));
                }
            }
            System.out.println("Row added " + row);
            data.add(row);
        }

        tableview.setItems(data);
    }

    /**
     * Initializes the class with necessary resources and UI elements.
     * This method is called automatically when the FXML file is loaded.
     * @param url The URL for the object(s).
     * @param resourceBundle The resource bundle to be used for localization.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateContactComboBox();
    }
}
