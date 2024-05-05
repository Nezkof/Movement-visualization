package com.example.movement_visualisation.controllers;

import com.example.movement_visualisation.Object;
import com.example.movement_visualisation.Cell;
import com.example.movement_visualisation.enums.WarningCodes;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;


public class VisualizationController {
    @FXML private Text warningText;
    @FXML private Pane warningWindow;
    @FXML private VBox VBox;

    private final int[] WIDTH_INTERVAL = {10,30};
    private final int[] HEIGHT_INTERVAL = {10,16};
    private final int[] OBJECTSNUMBER_INTERVAL = {2,5};
    private final String[] INTERFACE_COLORS = {
            "#222831", //field
            "#E3FEF7", //cell_highlight
            "#31363F", //obstacle
            "#2d323b", //cell_stroke
            "#EEEEEE", //highlight_object_stroke
            "#76ABAE", //object_stroke
            "#f26065" // object_error
    };

    Scene scene;
    private int fieldHeight, fieldWidth;
    private int objectsNumber;
    private boolean isObstacles;
    private GridPane map;
    private boolean[][] bitMap;
    private Object[] objects;
    private Object selectedObject;
    private Map<Object, Cell> objectGoalMap;

    /*===================================================
                       ІНІЦІАЛІЗАЦІЯ
    ====================================================*/
    @FXML void initialize (int fieldHeight, int fieldWidth, int objectsNumber, boolean isObstacles, Scene scene) {
        this.fieldHeight = fieldHeight;
        this.fieldWidth = fieldWidth;
        this.objectsNumber = objectsNumber;
        this.isObstacles = isObstacles;
        this.map = new GridPane();
        this.objectGoalMap = new HashMap<>();

        validateData();

        this.objects = new Object[this.objectsNumber];
        this.bitMap = new boolean[this.fieldHeight][this.fieldWidth];
        this.scene = scene;

        startVisualization();
    }

    private void startVisualization() {
        generateMap();
        setObjects();
    }

    /*===================================================
                 ВАЛІДАЦІЯ ТА ОБРОБКА ПОМИЛОК
    ====================================================*/

    private void validateData() {
        ArrayList<Integer> codes = getWarningCode();
        if (codes.isEmpty())
            return;

        int delay = 500;
        for (int code : codes) {
            for (WarningCodes enumValue : WarningCodes.values()) {
                if (enumValue.getCode() == code) {
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

    private ArrayList<Integer> getWarningCode(){
        ArrayList<Integer> codes = new ArrayList<>();
        if (this.fieldWidth > WIDTH_INTERVAL[1]) codes.add(101);
        if (this.fieldWidth < WIDTH_INTERVAL[0]) codes.add(102);
        if (this.fieldHeight > HEIGHT_INTERVAL[1]) codes.add(201);
        if (this.fieldHeight < HEIGHT_INTERVAL[0]) codes.add(202);
        if (this.objectsNumber > OBJECTSNUMBER_INTERVAL[1]) codes.add(301);
        if (this.objectsNumber < OBJECTSNUMBER_INTERVAL[0]) codes.add(302);

        return codes;
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
    private void generateMap() {
        generateBitMap();
        setCells();
    }

    private void generateBitMap(){
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

    private void setCells() {
        for (int i = 0; i < this.fieldHeight; i++) {
            for (int j = 0; j < this.fieldWidth; j++) {
                Cell cell = new Cell(50, 50);
                configureCell(cell, i, j);
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

    private void configureCell(Cell cell, int i, int j){
        if (!bitMap[i][j]) {
            cell.setFill(Color.valueOf(INTERFACE_COLORS[0]));
        }
        if (bitMap[i][j]) {
            cell.setAsObstacle(true);
            cell.setFill(Color.valueOf(INTERFACE_COLORS[2]));
        }

        cell.setOnMouseEntered(this::handleMouseEntered);
        cell.setOnMouseExited(this::handleMouseExited);

        cell.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                handleMouseRightClicked(event);
            } else {
                handleMouseClicked(event);
            }
        });

        cell.setStroke(Color.valueOf(INTERFACE_COLORS[3]));
    }

    private void setObjects() {
        Random random = new Random();
        for (int i = 0; i < objects.length; ++i){
            int x, y;
            do {
                x = random.nextInt(this.fieldWidth);
                y = random.nextInt(this.fieldHeight);
            } while (bitMap[y][x]);

            objects[i] = new Object(x, y);

            map.add(objects[i].getIcon(), x, y);
            GridPane.setHalignment(objects[i].getIcon(), HPos.CENTER);
            GridPane.setValignment(objects[i].getIcon(), VPos.CENTER);

            placeObjectObstacles(objects[i]);
        }

        selectedObject = objects[0];

        setObjectClickListeners();
    }

    private void placeObjectObstacles(Object object) {
        Cell cell = (Cell) map.getChildren().get(object.getY() * map.getColumnCount() + object.getX());
        cell.setAsObjectCell(true);
        cell.setAsObstacle(true);
        bitMap[object.getY()][object.getX()] = true;
    }

    /*===================================================
                   ОБРОБНИКИ ПОДІЙ ДЛЯ МИШІ
    ====================================================*/

    private void handleMouseEntered(MouseEvent event) {
        Cell cell = (Cell) event.getSource();
        Platform.runLater(() -> {
            if (!cell.isObstacle() || cell.isGoal())
                cell.setStroke(Color.valueOf(INTERFACE_COLORS[1]));
        });
    }

    private void handleMouseExited(MouseEvent event) {
        Cell cell = (Cell) event.getSource();
        Platform.runLater(() -> {
            if (!cell.isObstacle() || cell.isGoal())
                cell.setStroke(Color.valueOf(INTERFACE_COLORS[3]));
        });
    }

    private void handleMouseClicked(MouseEvent event) {
        if (selectedObject.isEnable()){
            Cell cell = (Cell) event.getSource();
            if (!cell.isObstacle() && !cell.isGoal()) {
                boolean isObjectCell = false;
                for (Object object : objects) {
                    if (object.getX() == GridPane.getColumnIndex((Node) event.getSource())
                            && object.getY() == GridPane.getRowIndex((Node) event.getSource())) {
                        isObjectCell = true;
                        break;
                    }
                }

                Cell objectCell = selectedObject.getCurrentObjectCell(map);
                if (!isObjectCell)
                    if (!objectGoalMap.containsValue(cell)) {
                        cell.setAsGoal(true);
                        objectGoalMap.put(selectedObject, cell);

                        selectedObject.startSearching(map, bitMap, objectGoalMap, objectCell, objectGoalMap.get(selectedObject), objectCell);
                    }
                    else {
                        selectedObject.getIcon().setFill(Color.valueOf(INTERFACE_COLORS[6]));
                    }
            }
        }
    }

    private void handleMouseRightClicked(MouseEvent event) {
        Cell cell = (Cell) event.getSource();
        if (!cell.isGoal() && !cell.isObstacle()) {
            cell.setAsObstacle(true);
            bitMap[GridPane.getRowIndex(cell)][GridPane.getColumnIndex(cell)] = true;
            cell.setFill(Color.valueOf(INTERFACE_COLORS[2]));
            cell.setStroke(Color.valueOf(INTERFACE_COLORS[3]));
        } else if (!cell.isGoal()) {
            cell.setAsObstacle(false);
            bitMap[GridPane.getRowIndex(cell)][GridPane.getColumnIndex(cell)] = false;
            cell.setFill(Color.valueOf(INTERFACE_COLORS[0]));
            cell.setStroke(Color.valueOf(INTERFACE_COLORS[3]));
        }
    }

    private void setObjectClickListeners() {
        for (Object object : objects) {
            Thread objectThread = new Thread(() -> object.getIcon().setOnMouseClicked(event -> {
                if (!object.isEnable())
                    return;
                Platform.runLater(() -> {
                    selectedObject.setIconStroke(Color.valueOf(INTERFACE_COLORS[5]));
                    selectedObject = object;
                    selectedObject.setIconStroke(Color.valueOf(INTERFACE_COLORS[4]));

                    selectedObject.move(scene, map, bitMap);
                });
            }));
            objectThread.start();
        }
    }

}
