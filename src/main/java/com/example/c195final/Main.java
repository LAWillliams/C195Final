package com.example.c195final;

import com.example.c195final.helper.CountriesQuery;
import com.example.c195final.helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("com/example/c195final/Login.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Parent root=FXMLLoader.load(getClass().getResource("/com/example/c195final/Login.fxml"));
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();
    }


    public static void main(String[] args) throws SQLException {

        JDBC.openConnection();

        launch(args);

        JDBC.closeConnection();
    }


}