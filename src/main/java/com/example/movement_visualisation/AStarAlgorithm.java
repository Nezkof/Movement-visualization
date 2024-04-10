package com.example.movement_visualisation;

import com.example.movement_visualisation.Cell;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;

public class AStarAlgorithm {
    private int MAX_STEPS;
    private GridPane map;
    private ArrayList<Cell> openList;
    private ArrayList<Cell> checkedList;
    private boolean goalReached;
    private Cell currentCell;
    private Cell startCell;
    private Cell goalCell;

    public AStarAlgorithm(){
        this.openList = new ArrayList<>();
        this.checkedList = new ArrayList<>();
        this.goalReached = false;
    }

    public AStarAlgorithm(Cell startCell, Cell goalCell, GridPane map){
        this.MAX_STEPS = map.getColumnCount()*map.getRowCount();
        this.currentCell = startCell;
        this.startCell = startCell;
        this.goalCell = goalCell;
        this.map = map;

        this.openList = new ArrayList<>();
        this.checkedList = new ArrayList<>();
        this.goalReached = false;
    }

    public void findPath(){
        int step = 0;
        while (!goalReached && step < MAX_STEPS){
            int col = GridPane.getColumnIndex(currentCell);
            int row = GridPane.getRowIndex(currentCell);

            currentCell.setAsChecked(true);
            checkedList.add(currentCell);
            openList.remove(currentCell);

            if (row-1 >= 0)
                openCell((Cell) map.getChildren().get((row-1)*map.getColumnCount() + col));

            if (col-1 >= 0)
                openCell((Cell) map.getChildren().get(row*map.getColumnCount() + (col-1)));

            if (row+1 < map.getRowCount())
                openCell((Cell) map.getChildren().get((row+1)*map.getColumnCount() + col));

            if (col+1 < map.getColumnCount())
                openCell((Cell) map.getChildren().get(row*map.getColumnCount() + (col+1)));

            int bestCellIndex = 0;
            int bestCellFCost = Integer.MAX_VALUE;

            for (int i = 0; i < openList.size(); ++i){
                if (openList.get(i).getFCost() < bestCellFCost) {
                    bestCellIndex = i;
                    bestCellFCost = openList.get(i).getFCost();
                }
                else if (openList.get(i).getFCost() == bestCellFCost) {
                    if (openList.get(i).getGCost() < openList.get(bestCellIndex).getGCost()) {
                        bestCellIndex = i;
                    }
                }
            }

            currentCell = openList.get(bestCellIndex);

            if (currentCell == goalCell){
                goalReached = true;
                trackThePath();
            }
            step++;
        }
    }

    private void trackThePath(){
        Cell current = goalCell;

        while(current != startCell){
            current = current.getParentCell();

            if (current != startCell) {
                current.setAsPath(true);
            }
        }
    }

    private void openCell(Cell cell) {
        if ((!cell.isOpen() && !cell.isChecked() && !cell.isObstacle()) || cell.isStart()){
            cell.getCost(startCell, goalCell);
            cell.setAsOpen(true);
            cell.setParent(currentCell);
            this.openList.add(cell);
        }
        else if (openList.isEmpty()){
            System.out.println("i dont know why");
            System.out.println("Size: " + openList.size());
            System.out.println("col: " + GridPane.getColumnIndex(cell) + " | row: " + GridPane.getRowIndex(cell));

        }
    }
}
