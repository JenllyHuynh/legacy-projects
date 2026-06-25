import java.util.Scanner;

/**
 * S02 String Array Manipulations
 * Manages string array operations including input, validation, sorting and display.
 * Handles a collection of names with proper formatting and sorting capabilities.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class StringArrayIO {
    // Array to store the names
    private String[] array;
    // Current size of the array (number of elements)
    private int size;
    // Define a constant for the maximum allowed array size
    // This prevents memory issues and enforces reasonable limits
    private static final int MAX_SIZE = 50;

    /**
     * Constructor to initialize the array with specified size.
     * Includes validation to ensure size is within acceptable bounds.
     *
     * @param size the maximum number of names the array can hold
     * @throws IllegalArgumentException if size is less than 1 or greater than MAX_SIZE
     */
    public StringArrayIO(int size) {
        // Validate minimum size requirement
        // Prevents creating arrays with zero or negative capacity
        if (size < 1) {
            // Throw exception with descriptive message for invalid size
            throw new IllegalArgumentException("Must be at least 1");
        }

        // Validate maximum size constraint
        // Prevents memory overflow and maintains performance
        if (size > MAX_SIZE) {
            // Include MAX_SIZE in error message for user clarity
            throw new IllegalArgumentException("Size cannot exceed " + MAX_SIZE);
        }

        // Assign validated size to instance variable
        // This stores the user-requested capacity
        this.size = size;

        // Initialize the string array with the validated size
        // Creates actual memory allocation for the names
        this.array = new String[size];  // Initialize array with given size
    }

    /**
     * Collects names from user input with validation.
     * Continuously prompts until valid names are entered.
     *
     * @param scanner the Scanner object for reading input
     */
    public void inputArray(Scanner scanner) {
        System.out.println("Enter " + size + " names: ");
        // Loop through each array position
        for (int i = 0; i < size; i++) {
            // Keep prompting until valid name is entered
            while (true) {
                String input = scanner.nextLine();  // Read user input

                // Validate the input name
                if (isValidName(input)) {
                    // Store normalized version of the name
                    array[i] = normalizeName(input);
                    break;  // Exit the validation loop for this name
                } else {
                    System.out.println("Invalid name!");
                }
            }
        }
    }

    /**
     * Validates if a name contains only letters and spaces.
     *
     * @param name the name string to validate
     * @return true if name is valid, false otherwise
     */
    private boolean isValidName(String name) {
        // Check for null input
        if (name == null) {
            return false;
        }
        // Normalize the name by trimming and collapsing multiple spaces
        String normalized = name.trim();
        // Check if the string 'normalized' is empty (has no characters at all).
        if (normalized.isEmpty()) {
            // If it is empty, immediately return false.
            return false;
        }
        // Check if the string 'normalized' contains only whitespace characters (spaces, tabs, etc.).
        if (normalized.matches("^\\s*$")) {
            // If the string is only whitespace, return false.
            return false;
        }

        // Check if name contains only letters and spaces using regex
        return normalized.matches("^[a-zA-Z\\s]+$");
    }

    /**
     * Normalizes a name by trimming spaces and collapsing multiple spaces.
     *
     * @param name the raw name string to normalize
     * @return the normalized name string
     */
    private String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");  // Trim and collapse spaces
    }

    /**
     * Displays the array contents with a title.
     *
     * @param title the header text to display before the list
     */
    public void displayArray(String title) {
        System.out.println(title);
        // Print each element with numbering
        for (int i = 0; i < size; i++) {
            System.out.println((i + 1) + ". " + array[i]);
        }
    }

    /**
     * Sorts the array alphabetically using bubble sort algorithm.
     * Case-insensitive comparison for proper alphabetical ordering.
     */
    public void bubbleSort() {
        // Outer loop for passes
        for (int i = 0; i < size - 1; i++) {
            // Inner loop for comparisons in each pass
            for (int j = 0; j < size - i - 1; j++) {
                // Compare adjacent elements (case-insensitive)
                if (array[j].compareToIgnoreCase(array[j + 1]) > 0) {
                    // Swap elements if they are in wrong order
                    String temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Provides access to the internal array.
     *
     * @return the string array containing all names
     */
    public String[] getArray() {
        return array;
    }
}