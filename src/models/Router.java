package models;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a router (node) in the network topology.
 * Each router maintains its routing table and knows about its neighbors.
 */
public class Router {
    private String name;
    private Point position; // For GUI visualization
    private Map<String, Integer> routingTable; // destination -> cost
    private Map<String, String> nextHopTable; // destination -> next hop router name
    private Map<Router, Integer> neighbors; // direct links and their costs
    
    public Router(String name, int x, int y) {
        this.name = name;
        this.position = new Point(x, y);
        this.routingTable = new HashMap<>();
        this.nextHopTable = new HashMap<>();
        this.neighbors = new HashMap<>();
        
        // Initialize routing table: distance to self is 0
        routingTable.put(name, 0);
        nextHopTable.put(name, name);
    }
    
    public Router(String name) {
        this(name, 100, 100); // Default position
    }
    
    public String getName() {
        return name;
    }
    
    public Point getPosition() {
        return position;
    }
    
    public void setPosition(int x, int y) {
        this.position = new Point(x, y);
    }
    
    public void setPosition(Point p) {
        this.position = new Point(p);
    }
    
    public Map<String, Integer> getRoutingTable() {
        return new HashMap<>(routingTable);
    }
    
    public Map<String, String> getNextHopTable() {
        return new HashMap<>(nextHopTable);
    }
    
    public Map<Router, Integer> getNeighbors() {
        return new HashMap<>(neighbors);
    }
    
    /**
     * Add a neighbor router with a direct link cost
     */
    public void addNeighbor(Router neighbor, int cost) {
        neighbors.put(neighbor, cost);
        // Update routing table if this is a better path
        String neighborName = neighbor.getName();
        if (!routingTable.containsKey(neighborName) || routingTable.get(neighborName) > cost) {
            routingTable.put(neighborName, cost);
            nextHopTable.put(neighborName, neighborName);
        }
    }
    
    /**
     * Remove a neighbor
     */
    public void removeNeighbor(Router neighbor) {
        neighbors.remove(neighbor);
        String neighborName = neighbor.getName();
        routingTable.remove(neighborName);
        nextHopTable.remove(neighborName);
    }
    
    /**
     * Update routing table entry
     */
    public void updateRoutingEntry(String destination, int cost, String nextHop) {
        routingTable.put(destination, cost);
        nextHopTable.put(destination, nextHop);
    }
    
    /**
     * Get cost to a destination
     */
    public Integer getCostTo(String destination) {
        return routingTable.get(destination);
    }
    
    /**
     * Get next hop for a destination
     */
    public String getNextHop(String destination) {
        return nextHopTable.get(destination);
    }
    
    /**
     * Check if router has a direct link to another router
     */
    public boolean hasNeighbor(Router router) {
        return neighbors.containsKey(router);
    }
    
    /**
     * Get the cost of direct link to a neighbor
     */
    public Integer getLinkCost(Router neighbor) {
        return neighbors.get(neighbor);
    }
    
    /**
     * Reset routing table to initial state (only self)
     */
    public void resetRoutingTable() {
        routingTable.clear();
        nextHopTable.clear();
        routingTable.put(name, 0);
        nextHopTable.put(name, name);
        
        // Re-add direct neighbors
        for (Map.Entry<Router, Integer> entry : neighbors.entrySet()) {
            String neighborName = entry.getKey().getName();
            int cost = entry.getValue();
            routingTable.put(neighborName, cost);
            nextHopTable.put(neighborName, neighborName);
        }
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Router router = (Router) obj;
        return name.equals(router.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

