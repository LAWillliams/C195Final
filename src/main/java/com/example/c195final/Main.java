package com.example.c195final;

import com.example.c195final.controller.CustomerController;
import com.example.c195final.helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Parent root=FXMLLoader.load(getClass().getResource("/com/example/c195final/Login.fxml"));
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) throws SQLException {

        JDBC.openConnection();

        //JDBC.getConnection();

        launch(args);

        JDBC.closeConnection();
    }


}