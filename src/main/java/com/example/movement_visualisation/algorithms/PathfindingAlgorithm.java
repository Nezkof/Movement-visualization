package com.example.movement_visualisation.algorithms;

import com.example.movement_visualisation.Cell;

import java.util.ArrayList;

abstract public class PathfindingAlgorithm {
    abstract public void findPath() throws Exception;

    public abstract ArrayList<Cell> getPath();
}
