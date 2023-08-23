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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

;

/**
 * @author lukea
 * */
public class LoginController implements Initializable {
    @FXML public Button loginButton;
    @FXML public TextField usernameField;
    @FXML public TextField passwordField;
    @FXML public Label location;
    @FXML public Label errorLabel;
    public static final Logger log = Logger.getLogger(LoginController.class.getName());
    ResourceBundle rb = ResourceBundle.getBundle("/com/example/c195final/Nat", Locale.getDefault());
    /**
     * This method queries the database for a username and password
     * */
    public static final String LOGIN_QUERY = "SELECT * FROM users WHERE User_Name = ? AND Password = ?";
    /**
     * This method uses the Login_query method to grab a username and password. Then stores them into a statement that eventually returns them as strings.
     * @param username takes a username as text input
     * @param password takes a password as text input
     * */
    public boolean isValidCredentials(String username, String password) {
        try (Connection connection = JDBC.getConnection();
             PreparedStatement statement = connection.prepareStatement(LOGIN_QUERY)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if there is at least one result
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * This method handles the login button action event. When a user clicks on the button it checks the username and password, if valid it will bring them to the main screen
     * @param event handles user input from mouse click
     * @throws IOException error handling
     * */
    @FXML
    public void loginButtonAction(ActionEvent event) throws IOException {

        JDBC.openConnection();
        // Store username and password into variables
        String username = usernameField.getText();
        String password = passwordField.getText();

        LoginController userAuthentication = new LoginController();
        boolean isValid = userAuthentication.isValidCredentials(username, password);

        logLoginActivity(username, isValid); // Log the login attempt

        if (isValid) {
            JDBC.openConnection();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/c195final/MainScreen.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            MainScreenController controller = loader.getController();
            // controller.sendProduct(productTableView.getSelectionModel().getSelectedIndex(),productTableView.getSelectionModel().getSelectedItem());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    /**
     * Logs login activity to a file.
     *
     * @param username The username associated with the login attempt.
     * @param isSuccess Indicates whether the login attempt was successful or not.
     */
    public void logLoginActivity(String username, boolean isSuccess) {
        try {
            FileHandler fileHandler = new FileHandler("login_activity.txt", true); // Append to the existing file
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            log.addHandler(fileHandler);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (isSuccess) {
                log.log(Level.INFO, "Successful login attempt: User = " + username + ", Timestamp = " + timestamp);
            } else {
                log.log(Level.WARNING, "Unsuccessful login attempt: User = " + username + ", Timestamp = " + timestamp);
            }

            fileHandler.close();

            // Print the path of the saved file
            String filePath = System.getProperty("user.dir") + "/login_activity.txt";
            System.out.println("Login activity log file saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /**
     * Initializes class
     * @param resourceBundle specifies resource bundle to be used for localization
     * @param url resolves location for the object(s)
     * */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try
        {
            JDBC.openConnection();
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);

            ZoneId zone = ZoneId.systemDefault();

            location.setText(String.valueOf(zone));

            System.out.println(rb.getString("hello") + " " + rb.getString("world"));


        } catch(MissingResourceException e) {
            System.out.println("Resource file missing: " + e);
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
