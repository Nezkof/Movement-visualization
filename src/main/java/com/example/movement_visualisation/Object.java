package com.example.movement_visualisation;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Scene;
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

    public void Move(Scene scene, GridPane map, boolean[][] bitMap){
        scene.setOnKeyPressed(event -> {
            if (!this.isEnable)
                return;
            icon.setFill(Color.valueOf("#76ABAE"));

            int newRow = this.y;
            int newCol = this.x;

            switch (event.getCode()) {
                case W:
                    newRow = this.y - 1;
                     break;
                case A:
                    newCol = this.x - 1;
                    break;
                case S:
                    newRow = this.y + 1;
                    break;
                case D:
                    newCol = this.x + 1;
                    break;
                default:
                    break;
            }

            if (isValidMove(newRow, newCol, map, bitMap)) {
                setCellAsObstacle(false, map, bitMap);
                map.getChildren().remove(this.icon);
                map.add(this.icon, newCol, newRow);
                this.setX(newCol);
                this.setY(newRow);
                setCellAsObstacle(true, map, bitMap);
            }
        });
    }

    private boolean isValidMove(int newRow, int newCol, GridPane map, boolean[][] bitMap) {
        return (newRow >= 0 && newRow < map.getRowCount() && newCol >= 0 && newCol < map.getColumnCount()) && !bitMap[newRow][newCol];
    }

    public void startSearching(GridPane map, boolean[][] bitMap, Map<Object, Cell> objectGoalMap, Cell startCell, Cell goalCell, Cell objectCell) {
        if (goalCell != null) {
            for (Node node : map.getChildren())
                if (node instanceof Cell)
                    ((Cell) node).resetCell();

        AStarAlgorithm algorithm = new AStarAlgorithm(startCell, goalCell, map);
        goalCell.setFill(Color.BLUE);
        try {
            algorithm.findPath();
        } catch (Exception e) {
            this.getIcon().setFill(Color.valueOf("#f26065"));
            this.setEnable(true);
            goalCell.resetCell();
            goalCell.setFill(Color.valueOf("#222831"));
            if (!objectGoalMap.isEmpty())
                objectGoalMap.clear();
            return;
        }

        this.followPath(algorithm.getPath(), map, bitMap, objectCell, objectGoalMap);
        }
    }

    public void followPath(ArrayList<Cell> path, GridPane map, boolean[][] bitMap, Cell objectCell, Map<Object, Cell> objectGoalMap) {
        isEnable = false;
        icon.setFill(Color.valueOf("#76ABAE"));

        if (path == null || path.isEmpty()) {
            icon.setFill(Color.valueOf("#f26065"));
            System.out.println("Пустий або невірний шлях");
            return;
        }

        Timeline timeline = new Timeline();
        boolean[] checkedPath = new boolean[path.size()];
        for (int i = path.size() - 1; i >= 0; --i) {
            int finalI = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5 * (path.size() - i)), event -> {
                setCellAsObstacle(false, map, bitMap);
                for (int cell = 0; cell < path.size(); ++cell) {
                    if (path.get(cell).isObstacle() && !checkedPath[cell]) {
                        this.startSearching(map, bitMap, objectGoalMap, this.getCurrentObjectCell(map), objectGoalMap.get(this), objectCell);
                        path.clear();
                    }
                }
                checkedPath[finalI] = true;
                if (!path.isEmpty()) {
                    this.setX(GridPane.getColumnIndex(path.get(finalI)));
                    this.setY(GridPane.getRowIndex(path.get(finalI)));
                    map.getChildren().remove(this.icon);
                    map.add(this.icon, GridPane.getColumnIndex(path.get(finalI)), GridPane.getRowIndex(path.get(finalI)));
                    setCellAsObstacle(true, map, bitMap);

                    if (objectGoalMap.containsValue(path.get(finalI))) {
                        for (Map.Entry<Object, Cell> entry : objectGoalMap.entrySet()) {
                            if (entry.getValue().equals(path.get(finalI))) {
                                objectGoalMap.remove(entry.getKey());
                                break;
                            }
                        }
                    }
                }

            }));
        }

        timeline.setOnFinished(e -> {
            isEnable = true;
        });
        timeline.play();
    }

    private void setCellAsObstacle(boolean value, GridPane map, boolean[][] bitMap) {
        Cell cell = (Cell)map.getChildren().get(y*map.getColumnCount()+x);
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

    /*===================================================
                          CЕТТЕРИ
    ====================================================*/
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
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
