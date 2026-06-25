import view.DictionaryView;

/**
 * S03 English – English dictionary
 * Main class - Entry point of the application
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Main {
    /**
     * Main method - Starts the dictionary application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create and start the dictionary user interface
        new DictionaryView().start();
    }
}