package gtfs.dijkstra;

import gtfs.models.StopTimes;
import gtfs.models.Stops;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node {

    public StopTimes stopTime;

    public Node(StopTimes stopTime) {
        this.stopTime = stopTime;
    }

    // shortestPath
    private List<Node> shortestPath = new LinkedList<>();
    public List<Node> getShortestPath() {
        return shortestPath;
    }
    public void setShortestPath(List<Node> shortestPath) { this.shortestPath = shortestPath; }

    // distance
    private Double distance = Double.MAX_VALUE;
    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    // adjacentNodes
    Map<Node, Double> adjacentNodes = new HashMap<>();
    public Map<Node, Double> getAdjacentNodes() {
        return adjacentNodes;
    }
    public void setAdjacentNodes(Map<Node, Double> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    public void addDestination(Node destination, double distance) {
        adjacentNodes.put(destination, distance);
    }

}