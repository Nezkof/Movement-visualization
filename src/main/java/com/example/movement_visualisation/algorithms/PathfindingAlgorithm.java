package com.example.movement_visualisation.algorithms;

import com.example.movement_visualisation.Cell;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

abstract public class PathfindingAlgorithm {
    protected int MAX_STEPS;
    protected GridPane map;
    protected Cell startCell;
    protected Cell goalCell;
    protected ArrayList<Cell> path;

    public PathfindingAlgorithm(Cell startCell, Cell goalCell, GridPane map) {
        this.MAX_STEPS = map.getColumnCount() * map.getRowCount();
        this.startCell = startCell;
        this.goalCell = goalCell;
        path = new ArrayList<>();
    }

    abstract public ArrayList<Cell> findPath();

    public abstract ArrayList<Cell> getPath();
}
