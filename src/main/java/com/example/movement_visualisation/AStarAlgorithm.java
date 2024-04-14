package com.example.movement_visualisation;

import javafx.scene.layout.GridPane;
import java.util.ArrayList;

public class AStarAlgorithm {
    private final int MAX_STEPS;
    private boolean goalReached;
    private final GridPane map;
    private Cell currentCell;
    private final Cell startCell;
    private final Cell goalCell;
    private final ArrayList<Cell> openList;
    private final ArrayList<Cell> path;

    public AStarAlgorithm(Cell startCell, Cell goalCell, GridPane map){
        this.MAX_STEPS = map.getColumnCount()*map.getRowCount();
        this.currentCell = startCell;
        this.startCell = startCell;
        this.goalCell = goalCell;
        this.map = map;

        this.openList = new ArrayList<>();
        this.path = new ArrayList<>();
        this.goalReached = false;
    }

    public void findPath() throws Exception {
        if (!path.isEmpty())
            path.clear();

        int step = 0;
        while (!goalReached && step < MAX_STEPS){
            int col = GridPane.getColumnIndex(currentCell);
            int row = GridPane.getRowIndex(currentCell);

            currentCell.setAsChecked(true);
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

            try {
                currentCell = openList.get(bestCellIndex);
            }
            catch (Exception ex) {
                throw new Exception("Can't reach the goal");
            }

            if (currentCell == goalCell){
                goalReached = true;
                trackThePath();
            }
            step++;
        }

        if (!goalReached)
            System.out.println("Шлях не знайдено");
    }

    private void trackThePath(){
        Cell current = goalCell;

        while(current != startCell) {
            this.path.add(current);
            current = current.getParentCell();

            if (current != startCell)
                current.setAsPath();
        }

        this.path.add(startCell);
    }

    private void openCell(Cell cell) {
        if ((!cell.isOpen() && !cell.isChecked() && !cell.isObstacle()) || cell.isStart()){
            cell.getCost(startCell, goalCell);
            cell.setAsOpen(true);
            cell.setParent(currentCell);
            this.openList.add(cell);
        }
    }

    public ArrayList<Cell> getPath() {
        return this.path;
    }
}
