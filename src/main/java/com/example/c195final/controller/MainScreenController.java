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

public class MainScreenController implements Initializable {

    /**
     * Functional interface for loading screens.
     */
    @FunctionalInterface
    interface ScreenLoader {
        void load(ActionEvent event, String resource);
    }


    /**
     * @LAMBDA
     * This LAMBDA creates a screen loader for loading new screens. Which is then used to load the FXML files for appointments, customers, and reports
     *
     * @param resource The resource location of the FXML file for the screen.
     * @return A ScreenLoader instance capable of loading the specified screen.
     */
    public ScreenLoader createScreenLoader(String resource) {
        return (event, res) -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(resource));
                Parent parent = loader.load();
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    @FXML
    public void mainMenuAppointmentAction(ActionEvent event) {
        ScreenLoader loader = createScreenLoader("/com/example/c195final/AppointmentView.fxml");
        loader.load(event, "/com/example/c195final/AppointmentView.fxml");
    }

    @FXML
    public void mainMenuCustomerAction(ActionEvent event) {
        ScreenLoader loader = createScreenLoader("/com/example/c195final/Customer.fxml");
        loader.load(event, "/com/example/c195final/Customer.fxml");
    }

    @FXML
    public void mainMenuReportAction(ActionEvent event) {
        ScreenLoader loader = createScreenLoader("/com/example/c195final/Reports.fxml");
        loader.load(event, "/com/example/c195final/Reports.fxml");
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
