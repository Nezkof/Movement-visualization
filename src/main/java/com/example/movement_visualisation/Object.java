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

public class Object {
    private int x;
    private int y;
    private final Circle icon;
    private boolean isEnable;
    private ArrayList<Cell> path;
    private PathfindingAlgorithm algorithm;
    private Cell goalCell;


    public Object(int x, int y){
        this.x = x;
        this.y = y;
        this.icon = new Circle(0,0,15, Color.valueOf("#76ABAE"));
        this.icon.setStroke(Color.valueOf("#31494a"));
        this.isEnable = true;
        this.path = new ArrayList<>();
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

    /*===================================================
                   РУХ ОБ'ЄКТА ПО АЛГОРИТМУ
    ====================================================*/

    public void startSearching(GridPane map, boolean[][] bitMap, Cell startCell, Cell objectCell) {
        if (goalCell == null) return;
        map.getChildren().stream().filter(node -> node instanceof Cell).forEach(node -> ((Cell) node).resetCell());

        algorithm = new AStarAlgorithm(startCell, goalCell, map);
        goalCell.setFill(Color.BLUE);

        path.clear();
        path = algorithm.findPath();

        if (path.isEmpty()) {
            this.setIconFill("#f26065");
            this.setEnable(true);
            goalCell.resetCell();

            if (!goalCell.isObstacle())
                goalCell.setFill(Color.valueOf("#222831"));
            else if (!goalCell.isObjectCell())
                goalCell.setFill(Color.valueOf("#31363F"));
        }

        this.followPath(map, bitMap, objectCell);

    }

    private void followPath(GridPane map, boolean[][] bitMap, Cell objectCell) {
        ArrayList<Cell> localPath = path;
        isEnable = false;
        this.setIconFill("#76ABAE");

        if (localPath == null || localPath.isEmpty()) {
            this.setIconFill("#f26065");
            isEnable = true;
            System.out.println("Пустий або невірний шлях");
            return;
        }

        Timeline timeline = new Timeline();
        boolean[] checkedPath = new boolean[localPath.size()];


        for (int i = localPath.size() - 1; i >= 0; --i) {
            int finalI = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5 * (localPath.size() - i)), event -> {
                setCellAsObstacle(false, map, bitMap);
                for (int j = 0; j < localPath.size(); ++j) {
                    if (localPath.get(j).isObstacle() && !checkedPath[j]) {
                        timeline.stop();
                        startSearching(map, bitMap, getCurrentObjectCell(map), objectCell);
                    }
                }

                checkedPath[finalI] = true;
                if (!localPath.isEmpty()) {
                    updatePosition(map, GridPane.getRowIndex(localPath.get(finalI)), GridPane.getColumnIndex(localPath.get(finalI)), bitMap);
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

    public void setGoalCell(Cell cell) {
        this.goalCell = cell;
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
