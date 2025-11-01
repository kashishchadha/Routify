package gui;

import models.NetworkGraph;
import models.Router;
import simulation.SimulationEngine;
import simulation.SimulationEngine.AlgorithmType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import models.Link;

/**
 * Main application window containing all panels and coordinating simulation.
 */
public class MainFrame extends JFrame {
    private NetworkGraph graph;
    private LeftPanel leftPanel;
    private CenterPanel centerPanel;
    private RightPanel rightPanel;
    private SimulationEngine simulationEngine;
    private JLabel statusLabel;
    
    public MainFrame() {
        graph = new NetworkGraph();
        simulationEngine = new SimulationEngine(graph);
        
        setTitle("Interactive Network Routing Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create panels
        centerPanel = new CenterPanel(graph);
        leftPanel = new LeftPanel(graph, centerPanel);
        rightPanel = new RightPanel(graph, centerPanel);
        
        // Listen for run algorithm event from left panel
        leftPanel.addPropertyChangeListener("runAlgorithm", e -> {
            int algorithmIndex = (Integer) e.getNewValue();
            runAlgorithm(algorithmIndex);
        });
        
        // Listen for graph changes to update source combo box
        leftPanel.addPropertyChangeListener("graphChanged", e -> {
            notifyGraphChanged();
        });
        
        setupGraphChangeListeners();
        
        // Add panels to frame
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        
        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        add(statusLabel, BorderLayout.SOUTH);
        
        // Set up simulation callbacks
        simulationEngine.setOnUpdate(this::onSimulationUpdate);
        simulationEngine.setOnComplete(this::onSimulationComplete);
        
        pack();
        setSize(1200, 700);
        setLocationRelativeTo(null);
    }
    
    private void setupGraphChangeListeners() {
        // We'll manually update when routers are added/removed
        // This could be improved with observer pattern, but for simplicity
        // we'll update periodically or on button clicks
    }
    
    public void notifyGraphChanged() {
        SwingUtilities.invokeLater(() -> {
            rightPanel.updateSourceComboBox();
        });
    }
    
    private void runAlgorithm(int algorithmIndex) {
        if (graph.getRouters().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please add at least one router to the network.",
                "Empty Network", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Router source = rightPanel.getSelectedSource();
        if (source == null && graph.getRouters().size() > 0) {
            source = graph.getRouters().get(0);
        }
        
        if (source == null) {
            return;
        }
        
        // Determine algorithm type
        AlgorithmType algorithmType;
        if (algorithmIndex == 0) {
            algorithmType = AlgorithmType.DISTANCE_VECTOR;
            statusLabel.setText("Running Distance Vector algorithm...");
        } else {
            algorithmType = AlgorithmType.LINK_STATE;
            statusLabel.setText("Running Link State algorithm...");
        }
        
        // Highlight source router
        Set<Router> highlight = new HashSet<>();
        highlight.add(source);
        centerPanel.highlightRouters(highlight);
        
        // Start simulation
        simulationEngine.startSimulation(algorithmType, source);
        
        // Update UI - disable controls during simulation
        leftPanel.setButtonsEnabled(false);
        rightPanel.setControlsEnabled(false);
    }
    
    private void onSimulationUpdate(Map<String, Map<String, Object>> allTables) {
        // Update routing tables display if visible
        SwingUtilities.invokeLater(() -> {
            rightPanel.updateRoutingTablesDisplay(allTables);
            centerPanel.repaint();
        });
    }
    
    private void onSimulationComplete() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Algorithm completed! Display routing tables to see results.");
            leftPanel.setButtonsEnabled(true);
            rightPanel.setControlsEnabled(true);
            
            // Show completion message
            JOptionPane.showMessageDialog(this, 
                "Algorithm execution completed!\nClick 'Display Routing Tables' to view results.",
                "Simulation Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

