package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import com.example.c195final.model.Appointment;
import java.sql.*;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.util.Callback;


public class AppointmentViewController implements Initializable {

    public ObservableList<ObservableList> data;
    public TableView tableview;

    @FXML
    public TextField appointmentDeleteField;

    @FXML
    public TextField appointmentUpdateField;

    ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static final ZoneId ET_ZONE = ZoneId.of("America/New_York"); // Eastern Time Zone
    private static final ZoneOffset UTC_OFFSET = ZoneOffset.UTC;

    /**
     * Handles the action when the "Back" button is clicked.
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If an I/O exception occurs.
     */
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
    public int appointmentDelete(int appointmentID) throws SQLException {
        JDBC.openConnection();
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    @FXML
    public void appointmentDeleteButton(ActionEvent event) throws SQLException {
        // Get the selected row from the TableView
        ObservableList<String> selectedRow = (ObservableList<String>) tableview.getSelectionModel().getSelectedItem();

        if (selectedRow != null) {
            String appointmentId = selectedRow.get(0); // Assuming Appointment_ID is the first column
            int deletedAppointmentID = Integer.parseInt(appointmentId);

            int rowsAffected = appointmentDelete(deletedAppointmentID);

            if (rowsAffected > 0) {
                // Remove the selected row from the data list
                data.remove(selectedRow);

                // Refresh the TableView
                tableview.refresh();
            }
        }
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

    @FXML
    public void appointmentUpdateButton(ActionEvent event) throws SQLException, IOException {
        ObservableList<String> selectedRow = (ObservableList<String>) tableview.getSelectionModel().getSelectedItem();

        if (selectedRow != null) {
            String appointmentID = selectedRow.get(0); // Assuming Appointment_ID is the first column
            int appointmentIdInt = Integer.parseInt(appointmentID);
            JDBC.openConnection();
            Appointment appointment = getAppointmentByID(appointmentIdInt);

            if (appointment != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195final/UpdateAppointment.fxml"));
                Parent parent = loader.load();
                Scene scene = new Scene(parent);

                UpdateAppointmentController controller = loader.getController();
                controller.setAppointment(appointment);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                // Handle no appointment record found
            }
        } else {
            // Handle no row selected
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

    public Instant convertToUTC(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().atOffset(UTC_OFFSET).toInstant();
    }

    // Convert UTC time to local time for display
    public LocalDateTime convertToLocal(Instant utcInstant) {
        return LocalDateTime.ofInstant(utcInstant, ZoneId.systemDefault());
    }

    public static final String Appointment_Query = "SELECT appointments.Appointment_ID,appointments.Title,appointments.Description,appointments.Location,appointments.Contact_ID,appointments.Type,appointments.Start,appointments.End,appointments.Customer_ID,appointments.User_ID from appointments";

    public void buildData() {
        data = FXCollections.observableArrayList();
        try {
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            PreparedStatement statement = connection.prepareStatement(Appointment_Query);
            ResultSet rs = statement.executeQuery();

            // Clear existing columns before adding new ones
            tableview.getColumns().clear();

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                tableview.getColumns().add(col);
                System.out.println("Column [" + i + "] ");
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

        JDBC.openConnection();
        buildData();

    }
}

