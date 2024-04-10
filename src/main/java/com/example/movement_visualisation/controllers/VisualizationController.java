package com.example.movement_visualisation.controllers;

import com.example.movement_visualisation.AStarAlgorithm;
import com.example.movement_visualisation.Object;
import com.example.movement_visualisation.Cell;
import com.example.movement_visualisation.enums.WarningCodes;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

import static java.lang.Integer.parseInt;

public class VisualizationController {
    @FXML private Text warningText;
    @FXML private Pane warningWindow;
    @FXML private VBox VBox;

    Scene scene;
    private int fieldHeight, fieldWidth;
    private int objectsNumber;
    private boolean isObstacles;
    private GridPane map;
    private boolean[][] bitMap;
    private Object[] objects;
    private Object selectedObject;
    private Cell goalCell, startCell;

    private final int[] WIDTH_INTERVAL = {10,30};
    private final int[] HEIGHT_INTERVAL = {10,16};
    private final int[] OBJECTSNUMBER_INTERVAL = {2,10};

    /*===================================================
                       ІНІЦІАЛІЗАЦІЯ
    ====================================================*/
    @FXML
    void initialize (int fieldHeight, int fieldWidth, int objectsNumber, boolean isObstacles, Scene scene) {
        this.fieldHeight = fieldHeight;
        this.fieldWidth = fieldWidth;
        this.objectsNumber = objectsNumber;
        this.isObstacles = isObstacles;
        this.map = new GridPane();

        validateData();

        this.objects = new Object[this.objectsNumber];
        this.bitMap = new boolean[this.fieldHeight][this.fieldWidth];
        this.scene = scene;

        startVisualization();
    }

    private void startVisualization() {
        generateMap();
        setObjects();
        setupObjectClickHandlers();


        // ========================
        // ДЛЯ ДЕБАГА
        // ========================

/*        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.001), event -> updateMapGraphics())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();*/
    }

    /*===================================================
                 ВАЛІДАЦІЯ ТА ОБРОБКА ПОМИЛОК
    ====================================================*/

    private void validateData() {
        String codes = getWarningCode();
        if (codes.isEmpty())
            return;

        String[] warningCodes = codes.split("\\s+");
        int delay = 500;

        for (String code : warningCodes) {
            int warningCode = parseInt(code);
            for (WarningCodes enumValue : WarningCodes.values()) {
                if (enumValue.getCode() == warningCode) {
                    String message = enumValue.getMessage(getOptimalValueForCode(enumValue));
                    addDelayMessage(message, delay);
                    setOptimalValueForCode(enumValue);
                    delay += 2100;
                    break;
                }
            }
        }
    }

    private int getOptimalValueForCode(WarningCodes code) {
        return switch (code) {
            case FIELD_WIDTH_TOO_HIGH -> WIDTH_INTERVAL[1];
            case FIELD_WIDTH_TOO_LOW -> WIDTH_INTERVAL[0];
            case FIELD_HEIGHT_TOO_HIGH -> HEIGHT_INTERVAL[1];
            case FIELD_HEIGHT_TOO_LOW -> HEIGHT_INTERVAL[0];
            case OBJECTS_NUMBER_TOO_HIGH -> OBJECTSNUMBER_INTERVAL[1];
            case OBJECTS_NUMBER_TOO_LOW -> OBJECTSNUMBER_INTERVAL[0];
        };
    }

    private void setOptimalValueForCode(WarningCodes code) {
        switch (code) {
            case FIELD_WIDTH_TOO_HIGH:
            case FIELD_WIDTH_TOO_LOW:
                this.fieldWidth = getOptimalValueForCode(code);
                break;
            case FIELD_HEIGHT_TOO_HIGH:
            case FIELD_HEIGHT_TOO_LOW:
                this.fieldHeight = getOptimalValueForCode(code);
                break;
            case OBJECTS_NUMBER_TOO_HIGH:
            case OBJECTS_NUMBER_TOO_LOW:
                objectsNumber = getOptimalValueForCode(code);
                break;
        }
    }

    private String getWarningCode(){
        StringBuilder resultCodes = new StringBuilder();
        if (this.fieldWidth > WIDTH_INTERVAL[1]) resultCodes.append("101 ");
        if (this.fieldWidth < WIDTH_INTERVAL[0]) resultCodes.append("102 ");
        if (this.fieldHeight > HEIGHT_INTERVAL[1]) resultCodes.append("201 ");
        if (this.fieldHeight < HEIGHT_INTERVAL[0]) resultCodes.append("202 ");
        if (this.objectsNumber > OBJECTSNUMBER_INTERVAL[1]) resultCodes.append("301 ");
        if (this.objectsNumber < OBJECTSNUMBER_INTERVAL[0]) resultCodes.append("302");

        return String.valueOf(resultCodes);
    }

    private void showWarningWindow(String msg) {
        Timer timer = new Timer();
        warningWindow.setVisible(true);
        warningText.setText(msg);
        timer.schedule(new TimerTask() {
            @Override public void run() { warningWindow.setVisible(false); }
        }, 2000);
    }

    private void addDelayMessage(String message, int delay) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(delay), e -> showWarningWindow(message)));
        timeline.play();
    }


    /*===================================================
                       ГЕНЕРАЦІЯ КАРТИ
    ====================================================*/
    void generateMap() {
        generateBitMap();
        setCells();
    }

    void generateBitMap(){
        Random random = new Random();
        for (int i = 0; i < this.fieldHeight; ++i) {
            for (int j = 0; j < this.fieldWidth; ++j) {
                if (isObstacles) {
                    for (int k = 0; k < this.fieldWidth/3; ++k)
                        bitMap[i][random.nextInt(this.fieldWidth)] = true;
                    break;
                }
                bitMap[i][j] = false;
            }
        }
    }

    void setCells() {
        for (int i = 0; i < this.fieldHeight; i++) {
            for (int j = 0; j < this.fieldWidth; j++) {
                Cell cell = new Cell(50, 50);

                if (!bitMap[i][j]) {
                    cell.setFill(Color.valueOf("#EEEEEE"));
                }
                if (bitMap[i][j]) {
                    cell.setAsObstacle(true);
                    cell.setFill(Color.valueOf("#31363F"));
                }

                cell.setOnMouseEntered(event -> {
                    if (!cell.isObstacle() || cell.isGoal())
                        cell.setFill(Color.valueOf("#bdbdbd"));
                });

                cell.setOnMouseExited(event -> {
                    if (!cell.isObstacle() && !cell.isGoal())
                        cell.setFill(Color.valueOf("#EEEEEE"));
                });

                cell.setOnMouseClicked(event -> {
                    if (!cell.isObstacle() && !cell.isGoal()) {
                        boolean isObjectCell = false;
                        for (Object object : objects) {
                            if (object.getX() == GridPane.getColumnIndex((Node) event.getSource())
                                    && object.getY() == GridPane.getRowIndex((Node) event.getSource())) {
                                isObjectCell = true;
                                break;
                            }
                        }

                        startCell = getCurrentObjectCell();

                        if (!isObjectCell) {
                            cell.setFill(Color.valueOf("#76ABAE"));
                            cell.setAsGoal(true);

                            if (goalCell != null) {
                                goalCell.setFill(Color.valueOf("#EEEEEE"));
                                goalCell.setAsGoal(false);
                            }
                            goalCell = cell;
                        }
                    }

                    startCell.setFill(Color.GREEN);
                    startSearching();
                });

                cell.setStroke(Color.valueOf("#222831"));
                map.add(cell, j, i);
                GridPane.setHalignment(cell, HPos.CENTER);
                GridPane.setValignment(cell, VPos.CENTER);
            }
        }

        StackPane stackPane = new StackPane(map);
        HBox hbox = new HBox(stackPane);
        hbox.setAlignment(Pos.CENTER);
        VBox.getChildren().add(hbox);
    }

    void setObjects() {
        Random random = new Random();
        for (int i = 0; i < objects.length; ++i){
            int[] cords = new int[2];

            do {
                cords[0] = random.nextInt(this.fieldWidth);
                cords[1] = random.nextInt(this.fieldHeight);
            } while (bitMap[cords[1]][cords[0]]);

            objects[i] = new Object(cords[0], cords[1]);

            map.add(objects[i].getIcon(), cords[0], cords[1]);
            GridPane.setHalignment(objects[i].getIcon(), HPos.CENTER);
            GridPane.setValignment(objects[i].getIcon(), VPos.CENTER);

            placeObjectObstacles(objects[i]);
        }

        selectedObject = objects[0];
    }

    private void placeObjectObstacles(Object object) {
        Cell cell = (Cell) map.getChildren().get(object.getY() * map.getColumnCount() + object.getX());
        cell.setAsObstacle(true);
        bitMap[object.getY()][object.getX()] = true;
    }

    private void setupObjectClickHandlers() {
        for (Object object : objects) {
            object.getIcon().setOnMouseClicked(event -> {
                selectedObject = object;
                selectedObject.Move(scene, map, bitMap);
            });
        }
    }

    public Cell getCurrentObjectCell() {
        for (Node node : map.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null) {
                if (GridPane.getColumnIndex(node) == selectedObject.getX() && GridPane.getRowIndex(node) == selectedObject.getY()) {
                    return (Cell) node;
                }
            }
        }
        return null;
    }

    //==========================================
    //
    // ТЕСТИ
    //
    //==========================================

    public void startSearching(){
        AStarAlgorithm algorithm = new AStarAlgorithm(startCell, goalCell, map);

        scene.setOnMouseClicked(mouseEvent ->  algorithm.findPath());

    }

    //==========================================
    //
    // ДЛЯ ДЕБАГА
    //
    //==========================================

    void updateMapGraphics() {
        for (int i = 0; i < fieldHeight; i++) {
            for (int j = 0; j < fieldWidth; j++) {
                Cell cell = (Cell) map.getChildren().get(i * fieldWidth + j);
                if (cell.isObstacle()) {
                    cell.setFill(Color.valueOf("#31363F"));
                } else {
                    cell.setFill(Color.valueOf("#EEEEEE"));
                }
            }
        }
    }



}
