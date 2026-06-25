import java.util.Scanner;

/**
 * S02 String Array Manipulations
 * Main class for String Array Manipulation Program.
 * Handles user input for array size and coordinates the sorting process.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Main {
    /**
     * Entry point of the application.
     * Repeatedly prompts for array size until valid input is provided,
     * then manages name input and sorting operations.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create Scanner for reading user input from console
        Scanner scanner = new Scanner(System.in);
        // Initialize array manager to null - will be created after validation
        StringArrayIO arrayManager = null;

        // Loop until successful creation of the array manager object
        while (arrayManager == null) {
            try {
                // Prompt user for array size
                System.out.print("Enter the value of n: ");
                // Read and parse the input as integer
                int n = Integer.parseInt(scanner.nextLine().trim());
                // Attempt to create array manager with validated size
                arrayManager = new StringArrayIO(n);
            } catch (NumberFormatException e) {
                // Handle non-numeric input
                System.out.println("Please enter a valid number.");
            } catch (IllegalArgumentException e) {
                // Handle size constraint violations
                System.out.println(e.getMessage());
            }
        }

        // Remainder of the program executes only after successful array creation
        arrayManager.inputArray(scanner);           // Collect names from user
        arrayManager.displayArray("List input name:");  // Display original names
        arrayManager.bubbleSort();                  // Sort names alphabetically
        arrayManager.displayArray("List sort name:");   // Display sorted names

    }
}