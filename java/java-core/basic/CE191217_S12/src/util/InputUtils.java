package util;

import java.util.Scanner;

/**
 * S12 - Convert binary, octal and hexadecimal to decimal
 * Utility class for handling and validating user input.
 * Provides methods for reading menu choices and number inputs.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class InputUtils {
    // Declare a static Scanner instance for reading input from console
    private static final Scanner sc = new Scanner(System.in);

    /**
     * Reads and validates a menu choice (1-4).
     * Ensures input is a number and within valid range.
     *
     * @return validated menu choice (1, 2, 3, or 4)
     */
    public static int readChoice() {
        while (true) {
            // Display prompt for menu choice
            System.out.print("Please choose number (1 – 4): ");
            try {
                // Read and parse input to integer
                int choice = Integer.parseInt(sc.nextLine().trim());
                // Validate choice is within 1-4 range
                if (choice >= 1 && choice <= 4) {
                    // Return valid choice
                    return choice;
                }
                // Invalid range error
                System.out.println("Invalid choice! Please enter 1, 2, 3 or 4.");
            } catch (NumberFormatException e) {
                // Non-numeric input error
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    /**
     * Reads a number input from the user.
     * Returns raw string input without validation (validation done in service).
     *
     * @return number string entered by user (trimmed)
     */
    public static String readNumber() {
        // Display prompt for number input
        System.out.print("Enter the number: ");
        // Read and return user input (trimmed)
        return sc.nextLine();
    }
}