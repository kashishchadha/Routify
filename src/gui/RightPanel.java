package gui;

import models.NetworkGraph;
import models.Router;
import models.Link;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Right panel with controls for link cost assignment, source selection, and routing table display.
 */
public class RightPanel extends JPanel {
    private NetworkGraph graph;
    private CenterPanel centerPanel;
    private JComboBox<String> sourceComboBox;
    private JButton assignCostButton;
    private JButton displayTablesButton;
    private JTextField costField;
    private JDialog routingTablesDialog;
    
    public RightPanel(NetworkGraph graph, CenterPanel centerPanel) {
        this.graph = graph;
        this.centerPanel = centerPanel;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Algorithm Settings"));
        setPreferredSize(new Dimension(250, 600));
        
        // Assign Link Cost button
        assignCostButton = new JButton("Assign Link Cost");
        assignCostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignLinkCost();
            }
        });
        
        // Cost input field
        JLabel costLabel = new JLabel("Cost:");
        costField = new JTextField("1", 5);
        JPanel costPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        costPanel.add(costLabel);
        costPanel.add(costField);
        
        // Source selection
        JLabel sourceLabel = new JLabel("Select Source Node:");
        sourceComboBox = new JComboBox<>();
        updateSourceComboBox();
        
        // Display Routing Tables button
        displayTablesButton = new JButton("Display Routing Tables");
        displayTablesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayRoutingTables();
            }
        });
        
        // Add components
        add(Box.createVerticalStrut(10));
        add(assignCostButton);
        add(Box.createVerticalStrut(10));
        add(costPanel);
        add(Box.createVerticalStrut(20));
        add(sourceLabel);
        add(sourceComboBox);
        add(Box.createVerticalStrut(10));
        add(displayTablesButton);
        add(Box.createVerticalGlue());
    }
    
    public void updateSourceComboBox() {
        sourceComboBox.removeAllItems();
        for (Router router : graph.getRouters()) {
            sourceComboBox.addItem(router.getName());
        }
        sourceComboBox.revalidate();
        sourceComboBox.repaint();
    }
    
    public void closeRoutingTablesDialog() {
        if (routingTablesDialog != null && routingTablesDialog.isVisible()) {
            routingTablesDialog.dispose();
        }
    }
    
    public Router getSelectedSource() {
        String selectedName = (String) sourceComboBox.getSelectedItem();
        if (selectedName == null) {
            return null;
        }
        return graph.getRouterByName(selectedName);
    }
    
    private void assignLinkCost() {
        if (graph.getRouters().size() < 2) {
            JOptionPane.showMessageDialog(this, 
                "Need at least 2 routers to assign link cost.",
                "Not Enough Routers", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String[] routerNames = graph.getRouters().stream()
            .map(Router::getName)
            .toArray(String[]::new);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Assign Link Cost", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));
        
        JLabel sourceLabel = new JLabel("Source Router:");
        JComboBox<String> sourceCombo = new JComboBox<>(routerNames);
        
        JLabel destLabel = new JLabel("Destination Router:");
        JComboBox<String> destCombo = new JComboBox<>(routerNames);
        
        JLabel costLabel = new JLabel("New Cost:");
        JTextField costInput = new JTextField("1");
        
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(e -> {
            try {
                String sourceName = (String) sourceCombo.getSelectedItem();
                String destName = (String) destCombo.getSelectedItem();
                int cost = Integer.parseInt(costInput.getText());
                
                if (sourceName.equals(destName)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Source and destination cannot be the same.",
                        "Invalid Link", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Router source = graph.getRouterByName(sourceName);
                Router dest = graph.getRouterByName(destName);
                
                // Update or create link
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
        dialog.add(costInput);
        dialog.add(okButton);
        dialog.add(cancelButton);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    public void displayRoutingTables() {
        if (graph.getRouters().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No routers in the network.",
                "Empty Network", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create dialog with routing tables
        if (routingTablesDialog != null) {
            routingTablesDialog.dispose();
        }
        
        routingTablesDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Routing Tables", false);
        routingTablesDialog.setLayout(new BorderLayout());
        
        // Create tabbed pane for each router
        JTabbedPane tabbedPane = new JTabbedPane();
        
        for (Router router : graph.getRouters()) {
            JPanel tablePanel = createRoutingTablePanel(router);
            tabbedPane.addTab(router.getName(), tablePanel);
        }
        
        routingTablesDialog.add(tabbedPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> routingTablesDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        routingTablesDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        routingTablesDialog.setSize(600, 400);
        routingTablesDialog.setLocationRelativeTo(this);
        routingTablesDialog.setVisible(true);
    }
    
    private JPanel createRoutingTablePanel(Router router) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table
        String[] columnNames = {"Destination", "Cost", "Next Hop"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        
        // Populate table - ensure all routers are shown
        Map<String, Integer> routingTable = router.getRoutingTable();
        Map<String, String> nextHopTable = router.getNextHopTable();
        
        // Get all routers in the network to ensure completeness
        java.util.List<models.Router> allRouters = graph.getRouters();
        
        // Sort destinations alphabetically for better readability
        java.util.List<String> destinations = new java.util.ArrayList<>(routingTable.keySet());
        java.util.Collections.sort(destinations);
        
        // If routing table doesn't have all routers, add missing ones
        for (models.Router r : allRouters) {
            String routerName = r.getName();
            if (!destinations.contains(routerName)) {
                destinations.add(routerName);
            }
        }
        java.util.Collections.sort(destinations);
        
        for (String destination : destinations) {
            Integer cost = routingTable.get(destination);
            String nextHop = nextHopTable.getOrDefault(destination, "-");
            
            // Format cost: show ∞ for infinity or unreachable
            String costStr;
            if (cost == null || cost == Integer.MAX_VALUE) {
                costStr = "∞";
                if (nextHop.equals("-")) {
                    nextHop = "-";
                }
            } else {
                costStr = String.valueOf(cost);
            }
            
            model.addRow(new Object[]{destination, costStr, nextHop});
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void setControlsEnabled(boolean enabled) {
        assignCostButton.setEnabled(enabled);
        displayTablesButton.setEnabled(enabled);
        sourceComboBox.setEnabled(enabled);
        costField.setEnabled(enabled);
    }
    
    public void updateRoutingTablesDisplay(Map<String, Map<String, Object>> allTables) {
        // This will be called during simulation to update the display
        if (routingTablesDialog != null && routingTablesDialog.isVisible()) {
            // Recreate tables with updated data
            displayRoutingTables();
        }
    }
}

