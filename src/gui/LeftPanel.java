package gui;

import models.NetworkGraph;
import models.Router;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Left panel with buttons for adding routers, links, and running algorithms.
 */
public class LeftPanel extends JPanel {
    private NetworkGraph graph;
    private CenterPanel centerPanel;
    private JButton addRouterButton;
    private JButton addLinkButton;
    private JButton runAlgorithmButton;
    private JComboBox<String> algorithmComboBox;
    
    private int routerCounter = 1;
    
    public LeftPanel(NetworkGraph graph, CenterPanel centerPanel) {
        this.graph = graph;
        this.centerPanel = centerPanel;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Controls"));
        setPreferredSize(new Dimension(200, 600));
        
        // Add Router button
        addRouterButton = new JButton("Add Router");
        addRouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRouter();
            }
        });
        
        // Add Link button
        addLinkButton = new JButton("Add Link");
        addLinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLink();
            }
        });
        
        // Algorithm selection
        JLabel algorithmLabel = new JLabel("Algorithm:");
        String[] algorithms = {"Distance Vector", "Link State"};
        algorithmComboBox = new JComboBox<>(algorithms);
        
        // Run Algorithm button
        runAlgorithmButton = new JButton("Run Algorithm");
        runAlgorithmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This will be handled by MainFrame
                firePropertyChange("runAlgorithm", null, algorithmComboBox.getSelectedIndex());
            }
        });
        
        // Add components with spacing
        add(Box.createVerticalStrut(10));
        add(addRouterButton);
        add(Box.createVerticalStrut(10));
        add(addLinkButton);
        add(Box.createVerticalStrut(20));
        add(algorithmLabel);
        add(algorithmComboBox);
        add(Box.createVerticalStrut(10));
        add(runAlgorithmButton);
        add(Box.createVerticalGlue());
    }
    
    private void addRouter() {
        // Generate router name
        String routerName = "R" + routerCounter;
        while (graph.getRouterByName(routerName) != null) {
            routerCounter++;
            routerName = "R" + routerCounter;
        }
        routerCounter++;
        
        // Add router at center of canvas
        int x = centerPanel.getWidth() / 2;
        int y = centerPanel.getHeight() / 2;
        if (x == 0) x = 400; // Default if panel not sized yet
        if (y == 0) y = 300;
        
        graph.addRouter(routerName, x, y);
        centerPanel.repaint();
        
        // Notify main frame to update source combo
        firePropertyChange("graphChanged", null, routerName);
    }
    
    private void addLink() {
        if (graph.getRouters().size() < 2) {
            JOptionPane.showMessageDialog(this, 
                "Need at least 2 routers to create a link.",
                "Not Enough Routers", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show dialog to select routers and cost
        String[] routerNames = graph.getRouters().stream()
            .map(Router::getName)
            .toArray(String[]::new);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Link", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));
        
        JLabel sourceLabel = new JLabel("Source Router:");
        JComboBox<String> sourceCombo = new JComboBox<>(routerNames);
        
        JLabel destLabel = new JLabel("Destination Router:");
        JComboBox<String> destCombo = new JComboBox<>(routerNames);
        
        JLabel costLabel = new JLabel("Link Cost:");
        JTextField costField = new JTextField("1");
        
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(e -> {
            try {
                String sourceName = (String) sourceCombo.getSelectedItem();
                String destName = (String) destCombo.getSelectedItem();
                int cost = Integer.parseInt(costField.getText());
                
                if (sourceName.equals(destName)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Source and destination cannot be the same.",
                        "Invalid Link", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Router source = graph.getRouterByName(sourceName);
                Router dest = graph.getRouterByName(destName);
                
                graph.addLink(source, dest, cost);
                centerPanel.repaint();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Invalid cost value.",
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(sourceLabel);
        dialog.add(sourceCombo);
        dialog.add(destLabel);
        dialog.add(destCombo);
        dialog.add(costLabel);
        dialog.add(costField);
        dialog.add(okButton);
        dialog.add(cancelButton);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    public int getSelectedAlgorithm() {
        return algorithmComboBox.getSelectedIndex();
    }
    
    public void setButtonsEnabled(boolean enabled) {
        addRouterButton.setEnabled(enabled);
        addLinkButton.setEnabled(enabled);
        runAlgorithmButton.setEnabled(enabled);
        algorithmComboBox.setEnabled(enabled);
    }
    
    public void notifyGraphChanged() {
        // Can be used to update UI when graph changes
    }
}

