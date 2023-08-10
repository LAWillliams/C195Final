package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import javafx.application.Application;
import javafx.application.Platform;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
     * */
    public static int appointmentDelete(int appointmentID) throws SQLException {
        String sql = "DELETE FROM customers WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,appointmentID);
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

    public void appointmentUpdateButton(ActionEvent event){}

    public static final String Appointment_Query = "SELECT appointments.Appointment_ID,appointments.Title,appointments.Description,appointments.Location,appointments.Contact_ID,appointments.Type,appointments.Start,appointments.End,appointments.Customer_ID,appointments.User_ID from appointments";

    public void buildData() {
        data = FXCollections.observableArrayList();
        try {
            //opens connection and executes query
            JDBC.openConnection();
            Connection connection = JDBC.getConnection();
            System.out.println(connection.isClosed());;
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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }


    public void initialize(URL url, ResourceBundle resourceBundle) {

        buildData();
        tableview = new TableView();
    }
}
