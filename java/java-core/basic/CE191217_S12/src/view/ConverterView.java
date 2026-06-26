package view;

import model.NumberConverter;
import service.ConverterService;
import util.InputUtils;

/**
 * S12 - Convert binary, octal and hexadecimal to decimal
 * View class for Number Converter System interface.
 * Provides menu-driven interface for number base conversions.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class ConverterView {
    // Declare a private final ConverterService instance for conversion operations
    private final ConverterService service = new ConverterService();

    /**
     * Starts the Number Converter System main loop.
     * Displays welcome message and handles menu navigation until exit.
     */
    public void start() {
        // Display program header
        System.out.println("=== Number System Converter ===\n");

        // Main program loop
        while (true) {
            // Display menu options
            displayMenu();
            // Read user choice from input
            int choice = InputUtils.readChoice();

            // Check if user wants to exit
            if (choice == 4) {
                System.out.println("Goodbye!");
                // Exit the program loop
                break;
            }

            // Read number input from user
            String input = InputUtils.readNumber();

            try {
                // Attempt conversion with the selected type
                NumberConverter result = service.convert(input, choice);
                // Display conversion result
                System.out.println(result + "\n");
            } catch (IllegalArgumentException e) {
                // Handle conversion errors
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }
    }

    /**
     * Displays the main menu options for Number System Converter.
     * Shows available conversion types and exit option.
     */
    private void displayMenu() {
        // Display conversion options
        System.out.println("1. Convert binary number to decimal number");
        System.out.println("2. Convert octal number to decimal number");
        System.out.println("3. Convert hexadecimal number to decimal number");
        // Display exit option
        System.out.println("4. Exit");
    }
}