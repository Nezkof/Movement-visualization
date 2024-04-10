package com.example.movement_visualisation;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Arrays;

public class Object {
    private int x;
    private int y;
    private final Circle icon;
    private boolean isSelected;

    Object() {
        this.x = 0;
        this.y = 0;
        this.icon = new Circle(0,0,10, Color.BLUE);
    }

    public Object(int x, int y){
        this.x = x;
        this.y = y;
        this.icon = new Circle(x,y, 10, Color.BLUE);
    }

    public void Move(Scene scene, GridPane map, boolean[][] bitMap){
            scene.setOnKeyPressed(event -> {
                int rowIndex = this.y;
                int colIndex = this.x;

                switch (event.getCode()) {
                    case W:
                        if (rowIndex > 0 && !bitMap[rowIndex - 1][colIndex]) {
                            setCellAsObstacle(false, map, bitMap);
                            map.getChildren().remove(this.icon);
                            map.add(this.icon, colIndex, rowIndex - 1);
                            this.setY(rowIndex - 1);
                            setCellAsObstacle(true, map, bitMap);
                        }
                        break;
                    case A:
                        if (colIndex > 0 && !bitMap[rowIndex][colIndex - 1]) {
                            setCellAsObstacle(false, map, bitMap);
                            map.getChildren().remove(this.icon);
                            map.add(this.icon, colIndex - 1, rowIndex);
                            this.setX(colIndex - 1);
                            setCellAsObstacle(true, map, bitMap);
                        }
                        break;
                    case S:
                        if (rowIndex < map.getRowCount() - 1 && !bitMap[rowIndex + 1][colIndex]) {
                            setCellAsObstacle(false, map, bitMap);
                            map.getChildren().remove(this.icon);
                            map.add(this.icon, colIndex, rowIndex + 1);
                            this.setY(rowIndex + 1);
                            setCellAsObstacle(true, map, bitMap);
                        }
                        break;
                    case D:
                        if (colIndex < map.getColumnCount() - 1 && !bitMap[rowIndex][colIndex + 1]) {
                            setCellAsObstacle(false, map, bitMap);
                            map.getChildren().remove(this.icon);
                            map.add(this.icon, colIndex + 1, rowIndex);
                            this.setX(colIndex + 1);
                            setCellAsObstacle(true, map, bitMap);
                        }
                        break;
                    default:
                        break;
                }
            });
    }


    public boolean isSelected() {
        return this.isSelected;
    }
    public int getX(){ return this.x; }

    public int getY(){ return this.y; }

    public Circle getIcon() { return this.icon;}

    public void setX(int x){ this.x = x; }

    public void setY(int y){ this.y = y; }
    public void setSelected(boolean value) {
        this.isSelected = value;
    }


    //==========================================
    //
    // ТЕСТИ
    //
    //==========================================

    private void setCellAsObstacle(boolean value, GridPane map, boolean[][] bitMap) {
        Cell cell = (Cell)map.getChildren().get(y*map.getColumnCount()+x);
        cell.setAsObstacle(value);
        bitMap[getY()][getX()] = value;
    }

}
