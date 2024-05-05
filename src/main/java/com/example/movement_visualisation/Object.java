package com.example.movement_visualisation;

import com.example.movement_visualisation.algorithms.AStarAlgorithm;
import com.example.movement_visualisation.algorithms.PathfindingAlgorithm;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Map;

public class Object {
    private int x;
    private int y;
    private final Circle icon;
    private boolean isEnable;

    public Object(int x, int y){
        this.x = x;
        this.y = y;
        this.icon = new Circle(0,0,15, Color.valueOf("#76ABAE"));
        this.icon.setStroke(Color.valueOf("#31494a"));
        this.isEnable = true;
    }

    /*===================================================
                           РУХ ОБ'ЄКТА
    ====================================================*/

    public void move(Scene scene, GridPane map, boolean[][] bitMap) {
        scene.setOnKeyPressed(event -> {
            if (!this.isEnable) return;
            icon.setFill(Color.valueOf("#76ABAE"));

            int newRow = this.y + (event.getCode() == KeyCode.S ? 1 : event.getCode() == KeyCode.W ? -1 : 0);
            int newCol = this.x + (event.getCode() == KeyCode.D ? 1 : event.getCode() == KeyCode.A ? -1 : 0);

            if (isValidMove(newRow, newCol, map, bitMap)) {
                updatePosition(map, newRow, newCol, bitMap);
            }
        });
    }

    private boolean isValidMove(int newRow, int newCol, GridPane map, boolean[][] bitMap) {
        return (newRow >= 0 && newRow < map.getRowCount() && newCol >= 0 && newCol < map.getColumnCount()) && !bitMap[newRow][newCol];
    }

    public void startSearching(GridPane map, boolean[][] bitMap, Map<Object, Cell> objectGoalMap, Cell startCell, Cell goalCell, Cell objectCell) {
        if (goalCell == null) return;
        map.getChildren().stream().filter(node -> node instanceof Cell).forEach(node -> ((Cell) node).resetCell());

        PathfindingAlgorithm algorithm = new AStarAlgorithm(startCell, goalCell, map);
        goalCell.setFill(Color.BLUE);

        try {
            algorithm.findPath();
        } catch (Exception e) {
            this.setIconFill("#f26065");
            this.setEnable(true);
            goalCell.resetCell();

            if (!goalCell.isObstacle())
                goalCell.setFill(Color.valueOf("#222831"));
            else if (!goalCell.isObjectCell())
                goalCell.setFill(Color.valueOf("#31363F"));

            objectGoalMap.clear();
        }

        this.followPath(algorithm.getPath(), map, bitMap, objectCell, objectGoalMap);
    }

    public void followPath(ArrayList<Cell> path, GridPane map, boolean[][] bitMap, Cell objectCell, Map<Object, Cell> objectGoalMap) {
        isEnable = false;
        this.setIconFill("#76ABAE");

        if (path == null || path.isEmpty()) {
            this.setIconFill("#f26065");
            System.out.println("Пустий або невірний шлях");
            return;
        }

        Timeline timeline = new Timeline();
        boolean[] checkedPath = new boolean[path.size()];

        for (int i = path.size() - 1; i >= 0; --i) {
            int finalI = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5 * (path.size() - i)), event -> {

                setCellAsObstacle(false, map, bitMap);
                for (int j = 0; j < path.size(); ++j) {
                    if (path.get(j).isObstacle() && !checkedPath[j]) {
                        startSearching(map, bitMap, objectGoalMap, getCurrentObjectCell(map), objectGoalMap.get(this), objectCell);
                        path.clear();
                    }
                }

                checkedPath[finalI] = true;
                if (!path.isEmpty()) {
                    updatePosition(map, GridPane.getRowIndex(path.get(finalI)), GridPane.getColumnIndex(path.get(finalI)), bitMap);

                    if (objectGoalMap.containsValue(path.get(finalI)))
                        for (Map.Entry<Object, Cell> entry : objectGoalMap.entrySet())
                            if (entry.getValue().equals(path.get(finalI))) {
                                objectGoalMap.remove(entry.getKey());
                                break;
                            }
                }

            }));
        }

        timeline.setOnFinished(e -> isEnable = true);
        timeline.play();
    }

    private void setCellAsObstacle(boolean value, GridPane map, boolean[][] bitMap) {
        Cell cell = (Cell)map.getChildren().get(y*map.getColumnCount()+x);
        cell.setAsObjectCell(value);
        cell.setAsObstacle(value);
        bitMap[getY()][getX()] = value;
    }

    public Cell getCurrentObjectCell(GridPane map) {
        for (Node node : map.getChildren())
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                GridPane.getColumnIndex(node) == this.getX() && GridPane.getRowIndex(node) == this.getY())
                    return (Cell) node;

        return null;
    }

    private void updatePosition(GridPane map, int newRow, int newCol, boolean[][] bitMap) {
        setCellAsObstacle(false, map, bitMap);
        map.getChildren().remove(this.icon);
        map.add(this.icon, newCol, newRow);
        this.x = newCol;
        this.y = newRow;
        setCellAsObstacle(true, map, bitMap);
    }

    /*===================================================
                          CЕТТЕРИ
    ====================================================*/
    private void setIconFill(String color) {
        this.getIcon().setFill(Color.valueOf(color));
    }
    public void setIconStroke(Color color) {
        this.icon.setStroke(color);
    }
    public void setEnable(boolean value) {
        this.isEnable = value;
    }

    /*===================================================
                          ГЕТТЕРИ
    ====================================================*/

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public Circle getIcon() {
        return this.icon;
    }
    public boolean isEnable() {
        return this.isEnable;
    }
}
