package util;

import java.util.Scanner;

/**
 * S04 - Simple Slot Machine
 * Utility class for handling user input with validation.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class InputUtils { // Public utility class for handling user input
    private static final Scanner sc = new Scanner(System.in); // Static final Scanner instance for reading console input

    /**
     * Reads a menu choice from the console, validates it, and handles input errors.
     *
     * @return The valid choice (1, 2, or 3) if input is successful; otherwise, returns -1
     * to indicate an invalid input or format error.
     */
    public static int readChoice() { // Static method to read and validate menu choice
        System.out.print("Choose one of the following menu options: "); // Display prompt message
        try { // Try block to handle potential NumberFormatException
            int choice = Integer.parseInt(sc.nextLine().trim()); // Read input, trim whitespace, convert to integer
            if (choice >= 1 && choice <= 3) return choice; // Validate if choice is between 1 and 3 inclusive
        } catch (Exception ignored) { // Catch any exception (mainly NumberFormatException)
            // Exception is ignored - no action needed as error message will be shown below
        }
        System.out.println("Invalid choice! Please enter 1, 2, or 3."); // Display error message for invalid input
        return -1; // Return -1 to indicate invalid input
    }
}