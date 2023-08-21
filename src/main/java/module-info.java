module com.example.c195final {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.j;

    opens com.example.c195final to javafx.fxml;
    exports com.example.c195final;

    opens com.example.c195final.controller to javafx.fxml;
    exports com.example.c195final.controller;

    opens com.example.c195final.helper to javafx.fxml;
    exports com.example.c195final.helper;

    //opens com.example.c195final.model to javafx.fxml;
    //exports com.example.c195final.model;

    opens com.example.c195final.dao to javafx.fxml;
    exports com.example.c195final.dao;
}