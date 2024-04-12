package com.example.movement_visualisation;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
    private Cell parentCell;
    private int gCost;
    private int hCost;
    private int fCost;
    private boolean isStart;
    private boolean isGoal;
    private boolean isOpen;
    private boolean isPath;
    private boolean isChecked;
    private boolean isObstacle;

    public Cell(double width, double height){
        super(width, height);
        this.isObstacle = false;
        this.isGoal = false;
    }

    public void calculateFCost(){
        this.fCost = this.hCost + this.gCost;
    }

    /*===================================================
                          СЕТТЕРИ
    ====================================================*/
    public void setAsObstacle(boolean value) {
        this.isObstacle = value;
    }
    public void setAsGoal(boolean value){
        this.isGoal = value;
    }
    public void setAsOpen(boolean value) {
        if (!this.isGoal)
            //this.setFill(Color.valueOf("#adafb1"));
            //this.setFill(Color.YELLOW);
        this.isOpen = value;
    }
    public void setAsChecked(boolean value){
        if (!this.isGoal && !this.isStart)
            //this.setFill(Color.valueOf("#7a7d82"));
            //this.setFill(Color.ORANGE);
        this.isChecked = value;
    }
    public void setAsPath(boolean value){
        if (!this.isGoal && !this.isStart)
            this.setFill(Color.valueOf("#454a51"));
            //this.setFill(Color.RED);
        this.isPath = value;
    }
    public void setParent(Cell parent) {
        this.parentCell = parent;
    }
    public void setGCost(int value) {
        this.gCost = value;
    }
    public void setHCost(int value) {
        this.hCost = value;
    }
    public void resetCell() {
        if (this.isOpen || this.isChecked || this.isGoal)
            this.setFill(Color.valueOf("#222831"));
        this.isOpen = false;
        this.isChecked = false;
        this.fCost = 0;
        this.gCost = 0;
        this.hCost = 0;
        this.isStart = false;
        this.isGoal = false;
        this.parentCell = null;
        //System.out.println("isObstacle: " + cell.isObstacle());
    }

    /*===================================================
                          ГЕТТЕРИ
    ====================================================*/

    public boolean isObstacle() {
        return this.isObstacle;
    }
    public boolean isGoal(){
        return this.isGoal;
    }
    public boolean isStart() {
        return this.isStart;
    }
    public boolean isOpen() {return this.isOpen;}
    public boolean isChecked() {return this.isChecked;}
    public int getFCost() {
        return this.fCost;
    }
    public int getHCost() {
        return this.hCost;
    }
    public int getGCost() {
        return this.gCost;
    }
    public Cell getParentCell(){
        return this.parentCell;
    }
    public void getCost(Cell startCell, Cell goalCell) {
        int xDist = Math.abs(GridPane.getColumnIndex(this) - GridPane.getColumnIndex(startCell));
        int yDist = Math.abs(GridPane.getRowIndex(this) - GridPane.getRowIndex(startCell));
        this.setGCost(xDist + yDist);

        xDist = Math.abs(GridPane.getColumnIndex(this) - GridPane.getColumnIndex(goalCell));
        yDist = Math.abs(GridPane.getRowIndex(this) - GridPane.getRowIndex(goalCell));
        this.setHCost(xDist + yDist);

        this.calculateFCost();
    }
}
