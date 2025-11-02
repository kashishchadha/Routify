package algorithms;

import models.NetworkGraph;
import models.Router;
import java.util.*;

/**
 * Implements Link State Routing Algorithm (Dijkstra's Algorithm).
 * Each router constructs the full topology and computes shortest paths.
 */
public class LinkState {
    private NetworkGraph graph;
    private Router sourceRouter;
    
    public LinkState(NetworkGraph graph) {
        this.graph = graph;
    }
    
    /**
     * Run Link State algorithm from a source router
     * Uses Dijkstra's algorithm to compute shortest paths
     */
    public void run(Router source) {
        this.sourceRouter = source;
        
        if (source == null || graph.getRouters().isEmpty()) {
            return;
        }
        
        // Reset routing table for source router
        source.resetRoutingTable();
        
        // Priority queue for Dijkstra's algorithm: (cost, router)
        PriorityQueue<DijkstraNode> queue = new PriorityQueue<>(
            Comparator.comparingInt(DijkstraNode::getCost)
        );
        
        // Distance from source to each router
        Map<Router, Integer> distances = new HashMap<>();
        Map<Router, Router> previous = new HashMap<>();
        Set<Router> visited = new HashSet<>();
        
        // Initialize distances: source has distance 0
        for (Router router : graph.getRouters()) {
            if (router.equals(source)) {
                distances.put(router, 0);
            } else {
                distances.put(router, Integer.MAX_VALUE);
            }
        }
        
        queue.add(new DijkstraNode(source, 0));
        
        // Dijkstra's algorithm
        while (!queue.isEmpty()) {
            DijkstraNode current = queue.poll();
            Router currentRouter = current.getRouter();
            
            if (visited.contains(currentRouter)) {
                continue;
            }
            
            visited.add(currentRouter);
            int currentDist = distances.get(currentRouter);
            
            // Explore neighbors
            for (Map.Entry<Router, Integer> neighborEntry : currentRouter.getNeighbors().entrySet()) {
                Router neighbor = neighborEntry.getKey();
                int linkCost = neighborEntry.getValue();
                
                if (visited.contains(neighbor)) {
                    continue;
                }
                
                int newDist = currentDist + linkCost;
                Integer oldDist = distances.get(neighbor);
                
                if (oldDist == null || newDist < oldDist) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentRouter);
                    queue.add(new DijkstraNode(neighbor, newDist));
                }
            }
        }
        
        // Update routing table for source router - include ALL routers
        for (Router router : graph.getRouters()) {
            if (router.equals(source)) {
                source.updateRoutingEntry(source.getName(), 0, source.getName());
                continue;
            }
            
            Integer dist = distances.get(router);
            if (dist != null && dist != Integer.MAX_VALUE) {
                // Find next hop to reach this destination
                Router dest = router;
                Router nextHop = dest;
                
                // Trace back to find first hop
                while (previous.get(dest) != null && !previous.get(dest).equals(source)) {
                    dest = previous.get(dest);
                    nextHop = dest;
                }
                
                if (previous.get(router) != null) {
                    nextHop = previous.get(router);
                }
                
                source.updateRoutingEntry(router.getName(), dist, nextHop.getName());
            } else {
                // Unreachable destination - set to infinity
                source.updateRoutingEntry(router.getName(), Integer.MAX_VALUE, "-");
            }
        }
        
        // Ensure all routers are in the table
        for (Router router : graph.getRouters()) {
            String routerName = router.getName();
            if (!source.getRoutingTable().containsKey(routerName)) {
                if (routerName.equals(source.getName())) {
                    source.updateRoutingEntry(routerName, 0, routerName);
                } else {
                    source.updateRoutingEntry(routerName, Integer.MAX_VALUE, "-");
                }
            }
        }
        
        // For Link State, all routers would compute their own shortest paths
        // In a full implementation, we'd run this for each router
        // For now, we'll compute it for the source router
    }
    
    /**
     * Run Link State for all routers (each computes its own shortest paths)
     */
    public void runForAllRouters() {
        for (Router router : graph.getRouters()) {
            run(router);
        }
    }
    
    /**
     * Get shortest path from source to destination
     */
    public List<Router> getShortestPath(Router destination) {
        if (sourceRouter == null) {
            return new ArrayList<>();
        }
        
        List<Router> path = new ArrayList<>();
        Router current = destination;
        
        // Build path backwards
        while (current != null) {
            path.add(0, current);
            
            String nextHopName = sourceRouter.getNextHop(current.getName());
            if (nextHopName == null || nextHopName.equals(sourceRouter.getName())) {
                break;
            }
            
            Router nextHop = graph.getRouterByName(nextHopName);
            if (nextHop == null || nextHop.equals(current)) {
                break;
            }
            
            current = nextHop;
        }
        
        return path;
    }
    
    /**
     * Helper class for Dijkstra's priority queue
     */
    private static class DijkstraNode {
        private Router router;
        private int cost;
        
        public DijkstraNode(Router router, int cost) {
            this.router = router;
            this.cost = cost;
        }
        
        public Router getRouter() {
            return router;
        }
        
        public int getCost() {
            return cost;
        }
    }
}

