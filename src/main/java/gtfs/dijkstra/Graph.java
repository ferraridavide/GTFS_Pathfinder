package gtfs.dijkstra;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Graph {

    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodeA.setShortestPath(new LinkedList<>());
        nodeA.setDistance(Double.MAX_VALUE);
        nodes.add(nodeA);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }
}