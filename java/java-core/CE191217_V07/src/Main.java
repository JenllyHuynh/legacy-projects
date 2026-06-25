import view.LuckyNumberView;

/**
 * V07 - The app should provide basic functionality. However, you can add
 *       additional functionality to your app if you wish.
 * Main application entry point for Lucky Number Game.
 * Launches the Lucky Number Game interface.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Main {
    /**
     * Main method to launch the Lucky Number Game.
     * Creates and starts the LuckyNumberView.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create a new LuckyNumberView instance and start the application
        new LuckyNumberView().start();
    }
}
