package com.example.movement_visualisation;

import com.example.movement_visualisation.Cell;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class AStarAlgorithm {
    private GridPane map;
    private ArrayList<Cell> openList;
    private ArrayList<Cell> checkedList;
    private boolean goalReached;
    private Cell currentCell;
    private Cell startCell;
    private Cell goalCell;

    AStarAlgorithm (){
        this.openList = new ArrayList<>();
        this.checkedList = new ArrayList<>();
        this.goalReached = false;
    }

    AStarAlgorithm (Cell currentCell, Cell startCell, Cell goalCell, GridPane map){
        this.currentCell = currentCell;
        this.startCell = startCell;
        this.goalCell = goalCell;
        this.map = map;

        this.openList = new ArrayList<>();
        this.checkedList = new ArrayList<>();
        this.goalReached = false;
    }

    public void findPath(){

        if (!goalReached){
            int col = GridPane.getColumnIndex(currentCell);
            int row = GridPane.getRowIndex(currentCell);

            currentCell.setAsChecked(true);
            checkedList.add(currentCell);
            openList.remove(currentCell);

            if (row-1 >= 0)
                openCell((Cell) map.getChildren().get(col*map.getColumnCount() + row-1));

            if (col-1 >= 0)
                openCell((Cell) map.getChildren().get((col-1)*map.getColumnCount() + row));

            if (row+1 < map.getRowCount())
                openCell((Cell) map.getChildren().get(col*map.getColumnCount() + row+1));

            if (col+1 < map.getColumnCount())
                openCell((Cell) map.getChildren().get((col+1)*map.getColumnCount() + row));

            int bestNodeIndex = 0;
            int bestNodeFCost = Integer.MAX_VALUE;

            for (int i = 0; i < openList.size(); ++i){
                if (openList.get(i).getFCost() < bestNodeFCost) {
                    bestNodeIndex = i;
                    bestNodeFCost = openList.get(i).getFCost();
                }
/*                else if (openList.get(i).getFCost() == bestNodeFCost) {
                    if (openList.get(i).getGCost()
                }*/
            }




        }
    }

    private void openCell(Cell cell) {
        if (!cell.isOpen() && !cell.isChecked() && !cell.isObstacle()){
            cell.setAsOpen(true);
            cell.setParent(currentCell);
            openList.add(cell);

        }
    }

    public void getCost(Cell startCell, Cell goalNode, Cell cell) {

        int xDist = Math.abs(GridPane.getColumnIndex(cell) - GridPane.getColumnIndex(startCell));
        System.out.println(GridPane.getColumnIndex(cell) + "-" + GridPane.getColumnIndex(startCell));

        int yDist = Math.abs(GridPane.getRowIndex(cell) - GridPane.getRowIndex(startCell));
        cell.setGCost(xDist + yDist);

        xDist = Math.abs(GridPane.getColumnIndex(cell) - GridPane.getColumnIndex(goalNode));
        yDist = Math.abs(GridPane.getRowIndex(cell) - GridPane.getRowIndex(goalNode));
        cell.setHCost(xDist + yDist);

        cell.calculateFCost();
    }
}
