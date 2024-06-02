package com.example.movement_visualisation.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
        @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader startMenu = new FXMLLoader(getClass().getResource("/com/example/movement_visualisation/StartMenu.fxml"));
        Scene scene = new Scene(startMenu.load(), 1536, 840);
        scene.getStylesheets().add(0, "file:/E:/KNU/2Course/2%20Semester/Курсова/Program/Movement-Visualization/src/main/resources/styles/styles.css");
        stage.getIcons().add(new Image("file:/E:/KNU/2Course/2%20Semester/Курсова/Program/Movement-Visualization/src/main/resources/assets/icon.png"));
        stage.setTitle("Movement Simulation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}