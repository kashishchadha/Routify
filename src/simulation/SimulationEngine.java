package simulation;

import algorithms.DistanceVector;
import algorithms.LinkState;
import models.NetworkGraph;
import models.Router;
import javax.swing.*;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Engine that runs routing algorithm simulations with animation support.
 * Uses Swing Timer for GUI updates.
 */
public class SimulationEngine {
    public enum AlgorithmType {
        DISTANCE_VECTOR,
        LINK_STATE
    }
    
    private NetworkGraph graph;
    private AlgorithmType currentAlgorithm;
    private Router sourceRouter;
    private Timer timer;
    private DistanceVector distanceVector;
    private LinkState linkState;
    
    private Consumer<Map<String, Map<String, Object>>> onUpdate;
    private Runnable onComplete;
    private boolean isRunning;
    
    public SimulationEngine(NetworkGraph graph) {
        this.graph = graph;
        this.isRunning = false;
    }
    
    /**
     * Set callback for routing table updates
     */
    public void setOnUpdate(Consumer<Map<String, Map<String, Object>>> callback) {
        this.onUpdate = callback;
    }
    
    /**
     * Set callback for when simulation completes
     */
    public void setOnComplete(Runnable callback) {
        this.onComplete = callback;
    }
    
    /**
     * Start simulation with specified algorithm and source
     */
    public void startSimulation(AlgorithmType algorithm, Router source) {
        if (isRunning) {
            stopSimulation();
        }
        
        this.currentAlgorithm = algorithm;
        this.sourceRouter = source;
        
        // Reset routing tables
        graph.resetRoutingTables();
        
        if (algorithm == AlgorithmType.DISTANCE_VECTOR) {
            startDistanceVectorSimulation();
        } else if (algorithm == AlgorithmType.LINK_STATE) {
            startLinkStateSimulation();
        }
    }
    
    private void startDistanceVectorSimulation() {
        distanceVector = new DistanceVector(graph);
        isRunning = true;
        
        timer = new Timer(500, e -> { // Update every 500ms
            if (!distanceVector.isConverged()) {
                distanceVector.runIteration();
                
                // Notify GUI to update
                if (onUpdate != null) {
                    Map<String, Map<String, Object>> tables = distanceVector.getAllRoutingTables();
                    onUpdate.accept(tables);
                }
            } else {
                // Simulation complete
                if (onComplete != null) {
                    onComplete.run();
                }
                stopSimulation();
            }
        });
        
        timer.start();
    }
    
    private void startLinkStateSimulation() {
        linkState = new LinkState(graph);
        isRunning = true;
        
        // For Link State, we can animate the computation
        Timer linkStateTimer = new Timer(300, e -> {
            // Run Link State algorithm
            linkState.run(sourceRouter);
            
            // Also compute for other routers for full view
            linkState.runForAllRouters();
            
            // Notify GUI to update
            if (onUpdate != null) {
                Map<String, Map<String, Object>> tables = new java.util.HashMap<>();
                for (Router router : graph.getRouters()) {
                    Map<String, Object> tableData = new java.util.HashMap<>();
                    tableData.put("routingTable", router.getRoutingTable());
                    tableData.put("nextHopTable", router.getNextHopTable());
                    tables.put(router.getName(), tableData);
                }
                onUpdate.accept(tables);
            }
            
            if (onComplete != null) {
                onComplete.run();
            }
            
            stopSimulation();
        });
        
        linkStateTimer.setRepeats(false); // Run once
        linkStateTimer.start();
        this.timer = linkStateTimer;
    }
    
    /**
     * Stop the simulation
     */
    public void stopSimulation() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        isRunning = false;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public AlgorithmType getCurrentAlgorithm() {
        return currentAlgorithm;
    }
    
    public Router getSourceRouter() {
        return sourceRouter;
    }
}

