package algorithms;

import models.NetworkGraph;
import models.Router;
import java.util.*;

/**
 * Implements Distance Vector Routing Algorithm (Bellman-Ford based).
 * Each router maintains a routing table with destination, cost, and next hop.
 * Routers exchange routing tables with neighbors until convergence.
 */
public class DistanceVector {
    private NetworkGraph graph;
    private boolean converged;
    private int iteration;
    
    public DistanceVector(NetworkGraph graph) {
        this.graph = graph;
        this.converged = false;
        this.iteration = 0;
    }
    
    /**
     * Run one iteration of Distance Vector algorithm
     * Returns true if converged, false otherwise
     */
    public boolean runIteration() {
        if (graph.getRouters().isEmpty()) {
            return true;
        }
        
        iteration++;
        boolean changed = false;
        
        // Each router receives routing tables from neighbors and updates its own
        for (Router router : graph.getRouters()) {
            // Get current routing table
            Map<String, Integer> currentTable = new HashMap<>(router.getRoutingTable());
            Map<String, String> currentNextHop = new HashMap<>(router.getNextHopTable());
            
            // Update routing table based on neighbors' tables
            for (Map.Entry<Router, Integer> neighborEntry : router.getNeighbors().entrySet()) {
                Router neighbor = neighborEntry.getKey();
                int linkCost = neighborEntry.getValue();
                
                // Get neighbor's routing table
                Map<String, Integer> neighborTable = neighbor.getRoutingTable();
                Map<String, String> neighborNextHop = neighbor.getNextHopTable();
                
                // For each destination in neighbor's table
                for (Map.Entry<String, Integer> destEntry : neighborTable.entrySet()) {
                    String destination = destEntry.getKey();
                    int neighborCostToDest = destEntry.getValue();
                    
                    // Skip if destination is this router itself
                    if (destination.equals(router.getName())) {
                        continue;
                    }
                    
                    // Calculate new cost: cost to neighbor + neighbor's cost to destination
                    int newCost = linkCost + neighborCostToDest;
                    
                    // Update if this is a better path or if we don't have a path yet
                    Integer currentCost = currentTable.get(destination);
                    if (currentCost == null || newCost < currentCost) {
                        currentTable.put(destination, newCost);
                        currentNextHop.put(destination, neighbor.getName());
                        changed = true;
                    } else if (currentCost.equals(newCost) && 
                               currentNextHop.get(destination).equals(neighbor.getName())) {
                        // Path might have changed even if cost is same
                        // (This handles the case where we update next hop)
                    }
                }
            }
            
            // Apply updates
            for (Map.Entry<String, Integer> entry : currentTable.entrySet()) {
                String dest = entry.getKey();
                int cost = entry.getValue();
                String nextHop = currentNextHop.get(dest);
                router.updateRoutingEntry(dest, cost, nextHop);
            }
        }
        
        converged = !changed;
        return converged;
    }
    
    /**
     * Run algorithm until convergence
     */
    public void runUntilConvergence() {
        converged = false;
        iteration = 0;
        
        // Reset routing tables first
        graph.resetRoutingTables();
        
        // Maximum iterations to prevent infinite loops (in case of negative cycles)
        int maxIterations = graph.getRouters().size() * 10;
        
        while (!converged && iteration < maxIterations) {
            runIteration();
        }
    }
    
    /**
     * Get routing tables for all routers
     */
    public Map<String, Map<String, Object>> getAllRoutingTables() {
        Map<String, Map<String, Object>> allTables = new HashMap<>();
        
        for (Router router : graph.getRouters()) {
            Map<String, Object> tableData = new HashMap<>();
            tableData.put("routingTable", router.getRoutingTable());
            tableData.put("nextHopTable", router.getNextHopTable());
            allTables.put(router.getName(), tableData);
        }
        
        return allTables;
    }
    
    public boolean isConverged() {
        return converged;
    }
    
    public int getIteration() {
        return iteration;
    }
}

