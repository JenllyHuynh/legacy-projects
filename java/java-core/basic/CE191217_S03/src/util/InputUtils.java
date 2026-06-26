package util;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * S03 English – English dictionary
 * Utility class for reading and validating user input
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class InputUtils {
    // Single scanner instance for all input operations
    private static final Scanner scanner = new Scanner(System.in);

    // Pattern to validate word format (only letters and single spaces)
    private static final Pattern VALID_WORD_PATTERN = Pattern.compile("^[a-zA-Z]+(\\s+[a-zA-Z]+)*$");

    /**
     * Reads and validates a word from user input
     * @return Validated and normalized word (lowercase)
     */
    public static String readWord() {
        while (true) {
            System.out.print("Enter word: ");
            // Read user input
            String input = scanner.nextLine();
            // Remove extra spaces (replace multiple spaces with single space)
            String cleaned = input.strip().replaceAll("\\s+", " ");

            // Check if input is empty
            if (cleaned.isEmpty()) {
                System.out.println("Error: Word cannot be empty!");
                continue;
            }

            // Validate against allowed pattern (letters and spaces only)
            if (!VALID_WORD_PATTERN.matcher(cleaned).matches()) {
                System.out.println("Error: Word can only contain letters and single spaces!");
                System.out.println("No numbers or special characters allowed!");
                continue;
            }

            // Confirm acceptance
            System.out.println("Accepted: " + cleaned);
            // Return normalized word (lowercase)
//            return cleaned.toLowerCase();
            return cleaned;
        }
    }

    /**
     * Reads and validates a meaning from user input
     * @return Validated meaning string
     */
    public static String readMeaning() {
        while (true) {
            System.out.print("Enter meaning: ");
            // Read and normalize meaning
            String meaning = scanner.nextLine().strip().replaceAll("\\s+", " ");

            // Meaning cannot be empty
            if (!meaning.isEmpty()) {
                return meaning;
            }
            System.out.println("Error: Meaning cannot be empty!");
        }
    }

    /**
     * Reads and validates menu choice
     * @return Valid menu choice (1-4) or -1 if invalid
     */
    public static int readChoice() {
        System.out.print("Please choose number (1 – 4): ");
        try {
            // Parse user input as integer
            int choice = Integer.parseInt(scanner.nextLine().trim());

            // Validate range
            if (choice >= 1 && choice <= 4) {
                return choice;
            }
        } catch (NumberFormatException e) {
            // Ignore parse errors, will show error message below
        }

        // Invalid input
        System.out.println("Invalid choice! Please enter a number from 1 to 4.");
        return -1;
    }
}