package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML
    private Button appointmentTypeAndMonthReportButton;

    @FXML
    private Button contactScheduleReportButton;

    @FXML
    private TextArea reportTextArea;

    @FXML
    public void reportsBackButtonAction(ActionEvent event) throws IOException {
        loadScreen("/com/example/c195final/MainScreen.fxml", event);
    }

    private void loadScreen(String resource, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        MainScreenController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void generateAppointmentTypeAndMonthReport(ActionEvent event) {
        try {
            JDBC.openConnection();
            String sql = "SELECT MONTH(Start) AS Month, Type, COUNT(*) AS Count FROM appointments GROUP BY Month, Type";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Create a StringBuilder to accumulate the report text
            StringBuilder reportText = new StringBuilder();

            // Process the result set and accumulate the report text
            while (rs.next()) {
                int month = rs.getInt("Month");
                String type = rs.getString("Type");
                int count = rs.getInt("Count");

                // Append the data to the reportText
                reportText.append("Month: ").append(month).append(", Type: ").append(type).append(", Count: ").append(count).append("\n");
            }

            // Display the report text in the TextArea
            reportTextArea.setText(reportText.toString());

            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void generateContactScheduleReport(ActionEvent event) {
        try {
            JDBC.openConnection();
            String sql = "SELECT c.Contact_Name, a.Appointment_ID, a.Title, a.Type, a.Description, a.Start, a.End, a.Customer_ID " +
                    "FROM appointments a " +
                    "JOIN contacts c ON a.Contact_ID = c.Contact_ID";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Create a StringBuilder to accumulate the report text
            StringBuilder reportText = new StringBuilder();

            // Process the result set and accumulate the report text
            while (rs.next()) {
                String contactName = rs.getString("Contact_Name");
                int appointmentId = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String type = rs.getString("Type");
                String description = rs.getString("Description");
                LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
                int customerId = rs.getInt("Customer_ID");

                // Append the data to the reportText
                reportText.append("Contact: ").append(contactName).append(", Appointment ID: ").append(appointmentId)
                        .append(", Title: ").append(title).append(", Type: ").append(type)
                        .append(", Description: ").append(description)
                        .append(", Start: ").append(start).append(", End: ").append(end)
                        .append(", Customer ID: ").append(customerId).append("\n");

                // Add other data fields as needed...
            }

            // Display the report text in the TextArea
            reportTextArea.setText(reportText.toString());

            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void generateCustomerDivisionReport(ActionEvent event) {
        try {
            JDBC.openConnection();
            String sql = "SELECT c.Customer_Name, d.Division FROM customers c " +
                    "JOIN first_level_divisions d ON c.Division_ID = d.Division_ID";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Create a StringBuilder to accumulate the report text
            StringBuilder reportText = new StringBuilder();

            // Process the result set and accumulate the report text
            while (rs.next()) {
                String customerName = rs.getString("Customer_Name");
                String division = rs.getString("Division");

                // Append the data to the reportText
                reportText.append("Customer: ").append(customerName).append(", Division: ").append(division).append("\n");
            }

            // Display the report text in the TextArea
            reportTextArea.setText(reportText.toString());

            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
