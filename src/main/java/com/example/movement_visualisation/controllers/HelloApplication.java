package com.example.movement_visualisation.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader startMenu = new FXMLLoader(getClass().getResource("/com/example/movement_visualisation/StartMenu.fxml"));
        Scene scene = new Scene(startMenu.load(), 1536, 840);
        stage.setTitle("Movement Simulation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}