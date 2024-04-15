package com.example.movement_visualisation.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;

public class HelloController {
    @FXML private TextField textFieldWidth;
    @FXML private TextField textFieldHeight;
    @FXML private TextField textFieldObjectsNumber;
    @FXML private Button startButton;
    @FXML private CheckBox button_isObstacles;
    @FXML private Text errorText;
    @FXML private Pane errorWindow;

    /*===================================================
                       ІНІЦІАЛІЗАЦІЯ
    ====================================================*/

    @FXML void initialize() {
        startButton.setOnAction(actionEvent -> {
            validateData();
            loadVisualizationWindow();
        });
    }

    /*===================================================
                ЗАВАНТАЖЕННЯ НАСТУПНОГО ВІКНА
    ====================================================*/

    void loadVisualizationWindow(){
        FXMLLoader visualizationWindow = new FXMLLoader(getClass().getResource("/com/example/movement_visualisation/VisualizationWindow.fxml"));
        try {
            Scene scene = new Scene(visualizationWindow.load(), 1536, 840);
            Stage stage = new Stage();
            stage.setTitle("Movement Simulation");
            stage.setScene(scene);

            VisualizationController visualizationController = visualizationWindow.getController();
            visualizationController.initialize (
                    parseInt(textFieldHeight.getText()),
                    parseInt(textFieldWidth.getText()),
                    parseInt(textFieldObjectsNumber.getText()),
                    button_isObstacles.isSelected(),
                    scene
            );
            stage.show();
            ((Stage) startButton.getScene().getWindow()).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*===================================================
                     ОБРОБКА ПОМИЛОК
    ====================================================*/
    void validateData() {
        switch (getErrorCode()) {
            case 101 : case 201: case 301:
                showErrorWindow("Всі поля налаштувань мають бути заповнені!");
                break;
            case 102 : case 202: case 302:
                showErrorWindow("Поля мають включати лише числові значення!");
                break;
            default:
                break;
        }
    }

    int getErrorCode() {
        if (textFieldHeight.getText().trim().isEmpty()) return 101;
        else if (!textFieldHeight.getText().trim().matches("\\d+")) return 102;

        if (textFieldWidth.getText().trim().isEmpty()) return 201;
        else if (!textFieldWidth.getText().trim().matches("\\d+")) return 202;

        if (textFieldObjectsNumber.getText().trim().isEmpty()) return 301;
        else if (!textFieldObjectsNumber.getText().trim().matches("\\d+")) return 302;

        return 0;
    }

    void showErrorWindow(String msg) {
        Timer timer = new Timer();
        errorWindow.setVisible(true);
        errorText.setText(msg);
        timer.schedule(new TimerTask() {
            @Override public void run() { errorWindow.setVisible(false); }
        }, 2000);
    }
}
