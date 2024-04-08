module com.example.movement_visualisation {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;

    opens com.example.movement_visualisation to javafx.fxml;
    exports com.example.movement_visualisation;
    opens com.example.movement_visualisation.enums;
    exports com.example.movement_visualisation.controllers;
    opens com.example.movement_visualisation.controllers to javafx.fxml;
}