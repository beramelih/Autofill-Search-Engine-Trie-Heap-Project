package autocomplete;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point — launches the Swing GUI on the Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default L&F.
        }
        AutocompleteGUI.launch();
    }
}
