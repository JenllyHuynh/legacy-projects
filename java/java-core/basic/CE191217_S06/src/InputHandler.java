import java.util.*; // Import Java utility classes including Scanner

/**
 * S06 - Delete duplicate elements in an array
 * Handles user input and coordinates the array processing workflow.
 * Manages application flow from size input to result display.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class InputHandler { // Main controller class for application flow
    // Scanner for reading user input
    private Scanner scanner; // Declare Scanner instance variable
    // ArrayProcessor for handling array operations
    private ArrayProcessor processor; // Declare ArrayProcessor instance variable

    /**
     * Constructor initializes the scanner for user input.
     */
    public InputHandler() { // Constructor method
        this.scanner = new Scanner(System.in); // Initialize scanner to read from console
    }

    /**
     * Validates and retrieves array size from user input.
     * Ensures size is positive integer within reasonable limits.
     *
     * @return validated array size
     */
    private int getValidSize() { // Private helper method for size validation
        while (true) { // Infinite loop until valid size is provided
            System.out.print("Please enter size of array: "); // Prompt user for input
            String input = scanner.nextLine().trim();  // Read and trim user input

            // Validate for empty input
            if (input == null || input.isEmpty()) { // Check if input is null or empty
                System.out.println("Error: Size cannot be empty.\n"); // Print error message
                continue; // Skip to next iteration of while loop
            }
            // Validate that input contains only digits
            if (!input.matches("\\d+")) { // Regex: one or more digits only
                System.out.println("Error: Size must be positive integer.\n"); // Print error message
                continue; // Skip to next iteration of while loop
            }

            int size = Integer.parseInt(input);  // Convert to integer

            // Validate size is positive
            if (size <= 0) { // Check if size is zero or negative
                System.out.println("Error: Size must be greater than 0.\n"); // Print error message
                continue; // Skip to next iteration of while loop
            }

            // Validate size is within reasonable limits
            if (size > 100) { // Check if size exceeds maximum limit
                System.out.println("Error: Maximum size is 100.\n"); // Print error message
                continue; // Skip to next iteration of while loop
            }

            return size;  // Return validated size
        }
    }

    /**
     * Main application workflow.
     * Gets array size, processes input, and displays results.
     */
    public void run() { // Main method that coordinates the application flow
        // Get validated array size from user
        int size = getValidSize(); // Call validation method
        // Initialize array processor with the validated size
        processor = new ArrayProcessor(size); // Create ArrayProcessor object with valid size
        // Collect array elements from user
        processor.inputArray(scanner); // Call method to input array elements
        // Display the original array
        processor.displayOriginalArray(); // Display array before processing
        // Process and display results (duplicates removed)
        processor.displayResults(); // Display array after duplicate removal
    }
}