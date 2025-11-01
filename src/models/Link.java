package models;

/**
 * Represents a link (edge) between two routers in the network.
 */
public class Link {
    private Router source;
    private Router destination;
    private int cost;
    
    public Link(Router source, Router destination, int cost) {
        this.source = source;
        this.destination = destination;
        this.cost = cost;
    }
    
    public Router getSource() {
        return source;
    }
    
    public Router getDestination() {
        return destination;
    }
    
    public int getCost() {
        return cost;
    }
    
    public void setCost(int cost) {
        this.cost = cost;
    }
    
    /**
     * Check if this link connects the given routers (in either direction)
     */
    public boolean connects(Router r1, Router r2) {
        return (source.equals(r1) && destination.equals(r2)) ||
               (source.equals(r2) && destination.equals(r1));
    }
    
    /**
     * Get the other router if one is provided
     */
    public Router getOther(Router router) {
        if (source.equals(router)) {
            return destination;
        } else if (destination.equals(router)) {
            return source;
        }
        return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Link link = (Link) obj;
        // Links are equal if they connect the same two routers (undirected)
        return (source.equals(link.source) && destination.equals(link.destination)) ||
               (source.equals(link.destination) && destination.equals(link.source));
    }
    
    @Override
    public int hashCode() {
        // Use symmetric hash code for undirected links
        return source.hashCode() + destination.hashCode();
    }
    
    @Override
    public String toString() {
        return source.getName() + " --" + cost + "--> " + destination.getName();
    }
}

