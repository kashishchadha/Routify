package models;

import java.awt.Point;
import java.util.*;

/**
 * Represents the entire network topology with routers and links.
 */
public class NetworkGraph {
    private List<Router> routers;
    private List<Link> links;
    
    public NetworkGraph() {
        this.routers = new ArrayList<>();
        this.links = new ArrayList<>();
    }
    
    public List<Router> getRouters() {
        return new ArrayList<>(routers);
    }
    
    public List<Link> getLinks() {
        return new ArrayList<>(links);
    }
    
    /**
     * Add a new router to the network
     */
    public Router addRouter(String name, int x, int y) {
        // Check if router with this name already exists
        Router existing = getRouterByName(name);
        if (existing != null) {
            return existing;
        }
        
        Router router = new Router(name, x, y);
        routers.add(router);
        return router;
    }
    
    /**
     * Remove a router and all its associated links
     */
    public void removeRouter(Router router) {
        if (router == null) return;
        
        // Remove all links connected to this router
        links.removeIf(link -> link.getSource().equals(router) || 
                            link.getDestination().equals(router));
        
        // Remove router from all neighbors
        for (Router r : routers) {
            if (r.hasNeighbor(router)) {
                r.removeNeighbor(router);
            }
        }
        
        routers.remove(router);
    }
    
    /**
     * Add a link between two routers
     */
    public Link addLink(Router source, Router destination, int cost) {
        if (source == null || destination == null) {
            return null;
        }
        
        if (source.equals(destination)) {
            return null; // Cannot link router to itself
        }
        
        // Check if link already exists
        Link existing = getLink(source, destination);
        if (existing != null) {
            existing.setCost(cost);
            // Update neighbor relationship
            source.addNeighbor(destination, cost);
            destination.addNeighbor(source, cost);
            return existing;
        }
        
        Link link = new Link(source, destination, cost);
        links.add(link);
        
        // Update neighbor relationships
        source.addNeighbor(destination, cost);
        destination.addNeighbor(source, cost);
        
        return link;
    }
    
    /**
     * Remove a link between two routers
     */
    public void removeLink(Router source, Router destination) {
        Link link = getLink(source, destination);
        if (link != null) {
            links.remove(link);
            source.removeNeighbor(destination);
            destination.removeNeighbor(source);
        }
    }
    
    /**
     * Get link between two routers (if exists)
     */
    public Link getLink(Router source, Router destination) {
        for (Link link : links) {
            if (link.connects(source, destination)) {
                return link;
            }
        }
        return null;
    }
    
    /**
     * Get router by name
     */
    public Router getRouterByName(String name) {
        for (Router router : routers) {
            if (router.getName().equals(name)) {
                return router;
            }
        }
        return null;
    }
    
    /**
     * Get router at a specific position (for GUI click detection)
     */
    public Router getRouterAt(int x, int y, int radius) {
        for (Router router : routers) {
            Point pos = router.getPosition();
            double distance = Math.sqrt(Math.pow(x - pos.x, 2) + Math.pow(y - pos.y, 2));
            if (distance <= radius) {
                return router;
            }
        }
        return null;
    }
    
    /**
     * Reset all routing tables
     */
    public void resetRoutingTables() {
        for (Router router : routers) {
            router.resetRoutingTable(routers);
        }
    }
    
    /**
     * Clear the entire network
     */
    public void clear() {
        routers.clear();
        links.clear();
    }
    
    /**
     * Check if the graph is connected
     */
    public boolean isConnected() {
        if (routers.isEmpty()) {
            return true;
        }
        
        Set<Router> visited = new HashSet<>();
        Queue<Router> queue = new LinkedList<>();
        queue.add(routers.get(0));
        visited.add(routers.get(0));
        
        while (!queue.isEmpty()) {
            Router current = queue.poll();
            for (Router neighbor : current.getNeighbors().keySet()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        
        return visited.size() == routers.size();
    }
}

