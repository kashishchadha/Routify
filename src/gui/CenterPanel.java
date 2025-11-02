package gui;

import models.Link;
import models.NetworkGraph;
import models.Router;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Central canvas panel for visualizing the network topology.
 * Handles drawing routers and links, and supports drag-and-drop.
 */
public class CenterPanel extends JPanel {
    private static final int ROUTER_RADIUS = 25;
    private static final int SELECTION_RADIUS = 30;
    
    private NetworkGraph graph;
    private Router selectedRouter;
    private Link selectedLink;
    private Router draggedRouter;
    private Point dragOffset;
    private boolean isDragging;
    
    // For visualization
    private Set<Router> highlightedRouters;
    private Set<Link> highlightedLinks;
    private Map<Router, Color> routerColors;
    
    // Callback for delete operations
    private Runnable onDeleteRequest;
    
    public CenterPanel(NetworkGraph graph) {
        this.graph = graph;
        this.selectedRouter = null;
        this.isDragging = false;
        this.highlightedRouters = new HashSet<>();
        this.highlightedLinks = new HashSet<>();
        this.routerColors = new HashMap<>();
        
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true); // Required for keyboard events
        
        // Mouse listeners for drag and drop
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow(); // Get focus for keyboard events
                
                Router clicked = graph.getRouterAt(e.getX(), e.getY(), SELECTION_RADIUS);
                if (clicked != null) {
                    selectedRouter = clicked;
                    selectedLink = null;
                    draggedRouter = clicked;
                    Point routerPos = clicked.getPosition();
                    dragOffset = new Point(e.getX() - routerPos.x, e.getY() - routerPos.y);
                    isDragging = true;
                    repaint();
                } else {
                    // Check if clicking on a link
                    Link clickedLink = getLinkAt(e.getX(), e.getY());
                    if (clickedLink != null) {
                        selectedLink = clickedLink;
                        selectedRouter = null;
                        draggedRouter = null;
                        repaint();
                    } else {
                        selectedRouter = null;
                        selectedLink = null;
                        repaint();
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isDragging && draggedRouter != null) {
                    int newX = e.getX() - dragOffset.x;
                    int newY = e.getY() - dragOffset.y;
                    // Keep router within bounds
                    newX = Math.max(ROUTER_RADIUS, Math.min(getWidth() - ROUTER_RADIUS, newX));
                    newY = Math.max(ROUTER_RADIUS, Math.min(getHeight() - ROUTER_RADIUS, newY));
                    draggedRouter.setPosition(newX, newY);
                    draggedRouter = null;
                    isDragging = false;
                    repaint();
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging && draggedRouter != null) {
                    int newX = e.getX() - dragOffset.x;
                    int newY = e.getY() - dragOffset.y;
                    // Keep router within bounds
                    newX = Math.max(ROUTER_RADIUS, Math.min(getWidth() - ROUTER_RADIUS, newX));
                    newY = Math.max(ROUTER_RADIUS, Math.min(getHeight() - ROUTER_RADIUS, newY));
                    draggedRouter.setPosition(newX, newY);
                    repaint();
                }
            }
        });
        
        // Keyboard listener for Delete key
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (selectedRouter != null || selectedLink != null) {
                        if (onDeleteRequest != null) {
                            onDeleteRequest.run();
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Find link at given coordinates (within a certain distance from the line)
     */
    private Link getLinkAt(int x, int y) {
        final int LINK_CLICK_TOLERANCE = 5; // pixels
        
        for (Link link : graph.getLinks()) {
            Router source = link.getSource();
            Router dest = link.getDestination();
            Point sourcePos = source.getPosition();
            Point destPos = dest.getPosition();
            
            // Calculate distance from point to line segment
            double distance = pointToLineDistance(x, y, 
                sourcePos.x, sourcePos.y, destPos.x, destPos.y);
            
            if (distance <= LINK_CLICK_TOLERANCE) {
                // Also check if point is within the line segment bounds
                int minX = Math.min(sourcePos.x, destPos.x);
                int maxX = Math.max(sourcePos.x, destPos.x);
                int minY = Math.min(sourcePos.y, destPos.y);
                int maxY = Math.max(sourcePos.y, destPos.y);
                
                if (x >= minX - LINK_CLICK_TOLERANCE && x <= maxX + LINK_CLICK_TOLERANCE &&
                    y >= minY - LINK_CLICK_TOLERANCE && y <= maxY + LINK_CLICK_TOLERANCE) {
                    return link;
                }
            }
        }
        return null;
    }
    
    /**
     * Calculate distance from a point to a line segment
     */
    private double pointToLineDistance(int px, int py, int x1, int y1, int x2, int y2) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;
        
        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = -1;
        
        if (lenSq != 0) {
            param = dot / lenSq;
        }
        
        double xx, yy;
        
        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }
        
        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public void setOnDeleteRequest(Runnable callback) {
        this.onDeleteRequest = callback;
    }
    
    public Router getSelectedRouter() {
        return selectedRouter;
    }
    
    public Link getSelectedLink() {
        return selectedLink;
    }
    
    public void setSelectedRouter(Router router) {
        this.selectedRouter = router;
        if (router != null) {
            this.selectedLink = null;
        }
        repaint();
    }
    
    public void setSelectedLink(Link link) {
        this.selectedLink = link;
        if (link != null) {
            this.selectedRouter = null;
        }
        repaint();
    }
    
    public void highlightRouters(Set<Router> routers) {
        this.highlightedRouters = new HashSet<>(routers);
        repaint();
    }
    
    public void highlightLinks(Set<Link> links) {
        this.highlightedLinks = new HashSet<>(links);
        repaint();
    }
    
    public void clearHighlights() {
        this.highlightedRouters.clear();
        this.highlightedLinks.clear();
        this.routerColors.clear();
        repaint();
    }
    
    public void setRouterColor(Router router, Color color) {
        if (color == null) {
            routerColors.remove(router);
        } else {
            routerColors.put(router, color);
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw links first (so they appear behind routers)
        for (Link link : graph.getLinks()) {
            drawLink(g2d, link);
        }
        
        // Draw routers
        for (Router router : graph.getRouters()) {
            drawRouter(g2d, router);
        }
    }
    
    private void drawLink(Graphics2D g, Link link) {
        Router source = link.getSource();
        Router dest = link.getDestination();
        Point sourcePos = source.getPosition();
        Point destPos = dest.getPosition();
        
        // Choose color based on highlight or selection
        if (link.equals(selectedLink)) {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(4));
        } else if (highlightedLinks.contains(link)) {
            g.setColor(Color.ORANGE);
            g.setStroke(new BasicStroke(3));
        } else {
            g.setColor(Color.GRAY);
            g.setStroke(new BasicStroke(2));
        }
        
        // Draw line
        g.drawLine(sourcePos.x, sourcePos.y, destPos.x, destPos.y);
        
        // Draw cost label in the middle of the link
        int midX = (sourcePos.x + destPos.x) / 2;
        int midY = (sourcePos.y + destPos.y) / 2;
        
        g.setColor(Color.BLACK);
        String costText = String.valueOf(link.getCost());
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(costText);
        int textHeight = fm.getHeight();
        
        // Draw background for text
        g.setColor(Color.WHITE);
        g.fillOval(midX - textWidth/2 - 3, midY - textHeight/2 - 2, textWidth + 6, textHeight + 4);
        
        g.setColor(Color.BLACK);
        g.drawString(costText, midX - textWidth/2, midY + textHeight/4);
    }
    
    private void drawRouter(Graphics2D g, Router router) {
        Point pos = router.getPosition();
        int x = pos.x - ROUTER_RADIUS;
        int y = pos.y - ROUTER_RADIUS;
        
        // Determine color
        Color fillColor;
        if (routerColors.containsKey(router)) {
            fillColor = routerColors.get(router);
        } else if (highlightedRouters.contains(router)) {
            fillColor = Color.YELLOW;
        } else if (router.equals(selectedRouter)) {
            fillColor = Color.CYAN;
        } else {
            fillColor = Color.LIGHT_GRAY;
        }
        
        // Draw router circle
        g.setColor(fillColor);
        g.fillOval(x, y, ROUTER_RADIUS * 2, ROUTER_RADIUS * 2);
        
        // Draw border
        if (router.equals(selectedRouter)) {
            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(3));
        } else {
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
        }
        g.drawOval(x, y, ROUTER_RADIUS * 2, ROUTER_RADIUS * 2);
        
        // Draw router name
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        String name = router.getName();
        int textWidth = fm.stringWidth(name);
        g.drawString(name, pos.x - textWidth/2, pos.y + fm.getAscent()/2 - 2);
    }
}

