package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

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
    public TextField Start;

    @FXML
    public TextField End;

    @FXML
    public TextField Customer_ID;

    @FXML
    public TextField User_ID;

    @FXML
    public TextField Contact_ID;

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

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Customer saved successfully.");
        alert.setContentText("The appointment data has been saved successfully.");
        alert.showAndWait();
    }

    private void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error occurred while saving appointment.");
        alert.setContentText(errorMessage);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Set minimum height to display full error message
        alert.showAndWait();
    }
    public int appointmentSaveAction(ActionEvent event) throws SQLException, IOException {
        String sql = "INSERT INTO appointments (Appointment_ID,Title,Description,Location,Type,Start,End,Customer_ID,Contact_ID,User_ID)VALUES(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,    Integer.parseInt(Appointment_ID.getText()));
        ps.setString(2, Title.getText());
        ps.setString(3, Description.getText());
        ps.setString(4, Location.getText());
        ps.setString(5, Type.getText());
        ps.setString(6, Start.getText());
        ps.setString(7, End.getText());
        ps.setInt(8,    Integer.parseInt(Customer_ID.getText()));
        ps.setInt(9,    Integer.parseInt(Contact_ID.getText()));
        ps.setInt(10,   Integer.parseInt(User_ID.getText()));
        int rowsAffected = ps.executeUpdate();

        if (rowsAffected == 1) {
            showSuccessAlert();
            appointmentCreateBackButton(event); // Go back to the Customer.fxml screen
        } else {
            showErrorAlert("Failed to save customer data.");
        };
        return rowsAffected;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
