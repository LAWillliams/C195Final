package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import com.example.c195final.model.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.IsoFields;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.util.Callback;
import javafx.stage.WindowEvent;

public class AppointmentViewController implements Initializable {

    public ObservableList<ObservableList> data;
    public TableView tableview;

    @FXML
    public TextField appointmentDeleteField;

    @FXML
    public TextField appointmentUpdateField;

    @FXML
    private ToggleGroup sortingToggleGroup;

    ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();


    @FXML
    public void appointmentBackButtonAction(ActionEvent event) throws IOException {

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
     */
    public static int appointmentDelete(int appointmentID) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    public void appointmentDeleteButton(ActionEvent event) throws SQLException {
        String appointmentID = appointmentDeleteField.getText();
        appointmentDelete(Integer.parseInt(appointmentID));
        tableview.refresh();
    }

    public void appointmentAddButton(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/c195final/AppointmentCreate.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        AppointmentCreateController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public static Appointment getAppointmentByID(int appointmentID) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentID);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int appointmentId = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String contactId = rs.getString("Contact_ID");
            String type = rs.getString("Type");
            String start = rs.getString("Start");
            String end = rs.getString("End");
            String customerId = rs.getString("Customer_ID");
            int userId = rs.getInt("User_ID");

            // Create a new Customer object with the retrieved data
            Appointment appointment = new Appointment(appointmentId, title, description, location, contactId, type, start, end, customerId, userId);
            return appointment;
        }

        // If no customer record was found with the given ID, return null
        return null;
    }

    public void appointmentUpdateButton(ActionEvent event) throws SQLException, IOException {

        String appointmentID = appointmentUpdateField.getText().trim(); // Trim to remove leading/trailing spaces

        // Check if the customerID is not empty or null
        if (!appointmentID.isEmpty()) {
            int appointmentIdInt = Integer.parseInt(appointmentID);

            // Retrieve the customer record using the provided customerID
            Appointment appointment = getAppointmentByID(appointmentIdInt);

            if (appointment != null) {
                // Load the new FXML screen
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/example/c195final/UpdateAppointment.fxml"));
                Parent parent = loader.load();
                Scene scene = new Scene(parent);

                // Pass the retrieved customer information to the controller of the new FXML screen
                UpdateAppointmentController controller = loader.getController();
                controller.setAppointment(appointment); // Assuming you have a setter in UpdateCustomerController

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

    public static final String AppointmentQueryByMonth = "SELECT appointments.Appointment_ID,appointments.Title,appointments.Description,appointments.Location,appointments.Contact_ID,appointments.Type,appointments.Start,appointments.End,appointments.Customer_ID,appointments.User_ID FROM appointments WHERE YEAR(appointments.Start) = YEAR(CURRENT_DATE) AND MONTH(appointments.Start) = MONTH(CURRENT_DATE)";


    @FXML
    public void sortByMonthRadioButton(ActionEvent event) {
        data = FXCollections.observableArrayList();

        try {
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            PreparedStatement statement = connection.prepareStatement(AppointmentQueryByMonth);
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
            JDBC.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    public static final String AppointmentQueryByWeek = "SELECT appointments.Appointment_ID,appointments.Title,appointments.Description,appointments.Location,appointments.Contact_ID,appointments.Type,appointments.Start,appointments.End,appointments.Customer_ID,appointments.User_ID FROM appointments WHERE YEAR(appointments.Start) = YEAR(CURRENT_DATE) AND MONTH(appointments.Start) = MONTH(CURRENT_DATE) AND WEEK(appointments.Start) = WEEK(CURRENT_DATE)";


    @FXML
    public void sortByWeekRadioButton(ActionEvent event) {
        data = FXCollections.observableArrayList();

        try {
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            PreparedStatement statement = connection.prepareStatement(AppointmentQueryByWeek);
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
            JDBC.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    @FXML
    public void sortAllButton(ActionEvent event){

        buildData();
    }

    public static final String Appointment_Query = "SELECT appointments.Appointment_ID,appointments.Title,appointments.Description,appointments.Location,appointments.Contact_ID,appointments.Type,appointments.Start,appointments.End,appointments.Customer_ID,appointments.User_ID from appointments";

    public void buildData() {
        data = FXCollections.observableArrayList();

        try {
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            PreparedStatement statement = connection.prepareStatement(Appointment_Query);
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
            JDBC.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }


    public void initialize(URL url, ResourceBundle resourceBundle) {

        buildData();

    }
}

