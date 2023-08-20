package com.example.c195final.controller;

import com.example.c195final.helper.JDBC;
import com.example.c195final.model.Appointment;
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

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Appointment saved successfully.");
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

    public void setAppointment(Appointment appointment) {
        Appointment_ID.setText(String.valueOf(appointment.getAppointmentId()));
        Title.setText(appointment.getTitle());
        Description.setText(appointment.getDescription());
        Location.setText(appointment.getLocation());
        Contact_ID.setText(appointment.getContactId());
        Type.setText(appointment.getType());
        Start.setText(appointment.getStart().toString()); // Convert LocalDateTime to String
        End.setText(appointment.getEnd().toString()); // Convert LocalDateTime to String
        Customer_ID.setText(appointment.getCustomerId());
        User_ID.setText(String.valueOf(appointment.getUserId()));

        // Set other relevant fields in the controller based on the Appointment object
    }



    public int appointmentUpdateSaveAction(ActionEvent event) throws SQLException, IOException {
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, Contact_ID = ?, User_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        try {
            ps.setInt(10, Integer.parseInt(Appointment_ID.getText()));
            ps.setInt(7, Integer.parseInt(Customer_ID.getText()));
            ps.setInt(8, Integer.parseInt(Contact_ID.getText()));
            ps.setInt(9, Integer.parseInt(User_ID.getText()));
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid integer input. Please provide valid integer values.");
            return 0; // Indicate that the operation failed
        }

        ps.setString(1, Title.getText());
        ps.setString(2, Description.getText());
        ps.setString(3, Location.getText());
        ps.setString(4, Type.getText());
        ps.setString(5, Start.getText());
        ps.setString(6, End.getText());

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
