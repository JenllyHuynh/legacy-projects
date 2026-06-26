import view.ConverterView;

/**
 * S12 - Convert binary, octal and hexadecimal to decimal
 * Main application entry point for Number Converter System.
 * Launches the Number Converter interface.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Main {
    /**
     * Main method to launch the Number Converter System.
     * Creates and starts the ConverterView.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create a new ConverterView instance and start the application
        new ConverterView().start();
    }
}