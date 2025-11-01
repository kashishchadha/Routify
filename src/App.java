import gui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the Interactive Network Routing Simulator application.
 */
public class App {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Launch the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

