package com.example.c195final.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author lukea
 * */
public class MainScreenController implements Initializable {
    /**
     * This method handles user input to load the appointment screen.
     * @param event takes user input in the form of a mouse click
     * @throws IOException handles errors
     * */
    @FXML
    public void mainMenuAppointmentAction(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/c195final/AppointmentView.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        AppointmentViewController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }
    /**
     * This method handles user input to load the customer screen.
     * @param event takes user input in the form of a mouse click
     * @throws IOException handles errors
     * */
    @FXML
    public void mainMenuCustomerAction(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/c195final/Customer.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        //CustomerController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }
    /**
     * This method handles user input to load the report screen.
     * @param event takes user input in the form of a mouse click
     * @throws IOException handles errors
     * */
    @FXML
    public void mainMenuReportAction(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/c195final/Reports.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        ReportsController controller = loader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
