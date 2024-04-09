package com.example.movement_visualisation;

import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
    private Cell parent;
    private int gCost;
    private int hCost;
    private int fCost;
    private boolean isStart;
    private boolean isGoal;
    private boolean isOpen;
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
        this.isOpen = value;
    }
    public void setAsChecked(boolean value){
        this.isChecked = value;
    }
    public void setParent(Cell parent) {
        this.parent = parent;
    }
    public void setGCost(int value) {
        this.gCost = value;
    }
    public void setHCost(int value) {
        this.hCost = value;
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




}
