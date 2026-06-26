import view.Menu;

/**
 * S01 Manage student
 * Main class serving as the entry point of the Student Management Application.
 * Follows the single responsibility principle - only responsible for starting the application.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Main {
    /**
     * Main method that starts the Student Management Application.
     *
     * @param args command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        new Menu().run();  // Create Menu instance and start the application
    }
}