import java.util.*;

/**
 * S06 - Delete duplicate elements in an array
 * Handles array operations including input, processing, and duplicate removal.
 * Provides multiple methods for removing duplicates from integer arrays.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class ArrayProcessor {
    // Size of the array
    private int size;
    // The integer array to process
    private int[] arr;

    /**
     * Constructor to initialize the array processor with specified size.
     *
     * @param size the size of the array to create
     */
    public ArrayProcessor(int size) {
        // Size of the array
        this.size = size;// Instance variable to store array size
        // The integer array to process
        this.arr = new int[size];  // Initialize array with given size
    }

    /**
     * Collects array elements one by one with comprehensive validation.
     * Validates for empty input, valid integers, and single number per input.
     *
     * @param scanner the Scanner object for reading input
     */
    public void inputArray(Scanner scanner) {
        // Loop through each array position
        for (int i = 0; i < size; i++) {
            // Keep prompting until valid input is received
            while (true) {
                System.out.printf("Element[%d] = ", i);  // Prompt for specific element
                String input = scanner.nextLine();  // Read user input

                // Validate for empty input
                if (input == null || input.trim().isEmpty()) { // Check if input is null or empty
                    System.out.println("Cannot be empty."); // Print error message
                    continue; // Skip to next iteration of while loop
                }
                String trimmed = input.trim();  // Remove leading/trailing spaces

                // Validate that input contains only digits (with optional minus sign)
                if (!trimmed.matches("-?\\d+")) { // Regex: optional minus sign followed by digits
                    System.out.println("Enter a valid integer."); // Print error message
                    continue; // Skip to next iteration of while loop
                }
                // Check for multiple numbers in one input by looking for spaces
                String collapsed = input.replaceAll("\\s+", " "); // Replace multiple spaces with single space
                if (collapsed.contains(" ")) { // Check if string contains any space
                    System.out.println("Enter one number."); // Print error message
                    continue; // Skip to next iteration of while loop
                }
                // Final validation with parseInt
                try { // Try block for Integer.parseInt
                    arr[i] = Integer.parseInt(trimmed);  // Convert to integer and store
                    break;  // Exit validation loop for this element
                } catch (Exception e) { // Catch any exception from parseInt
                    System.out.println("Enter a valid integer."); // Print error message
                }
            }
        }
    }

    /**
     * Displays the original array contents.
     */
    public void displayOriginalArray() {
        System.out.println("\nThe original array: "); // Print header
        // Print all elements in the array
        for (int i = 0; i < size; i++) { // Loop through array
            System.out.print(arr[i] + " "); // Print element followed by space
        }
        System.out.println();  // New line after displaying array
    }

    /**
     * Removes duplicates from array using HashSet for O(1) lookups.
     * Maintains insertion order using LinkedHashSet.
     *
     * @return array with duplicates removed
     */
    public int[] removeDuplicatesWithHashSet() {
        // Use LinkedHashSet to maintain insertion order while ensuring uniqueness
        Set<Integer> uniqueSet = new LinkedHashSet<>(); // Create LinkedHashSet
        // Add all elements to set (duplicates are automatically removed)
        for (int num : arr) { // Enhanced for loop through array
            uniqueSet.add(num); // Automatically remove duplicates
        }
        // Convert set back to array
        int[] result = new int[uniqueSet.size()]; // Create array with set size
        int index = 0; // Initialize index counter
        for (int num : uniqueSet) { // Loop through set elements
            result[index++] = num; // Assign element and increment index
        }
        return result; // Return unique array
    }

//    /**
//     * Removes duplicates from array using nested loops for comparison.
//     * This method is kept for performance comparison with HashSet approach.
//     * Time complexity: O(n²) - slower for large arrays
//     *
//     * @return array with duplicates removed
//     */
//    public int[] removeDuplicatesWithLoop() {
//        List<Integer> resultList = new ArrayList<>();
//        // For each element, check if it's already in the result list
//        for (int num : arr) {
//            if (!resultList.contains(num)) {
//                resultList.add(num);  // Add only if not already present
//            }
//        }
//        // Convert list to array
//        int[] result = new int[resultList.size()];
//        for (int i = 0; i < resultList.size(); i++) {
//            result[i] = resultList.get(i);
//
//        }
//        return result;
//    }

    /**
     * Displays the results after duplicate removal.
     * Currently, uses HashSet method for better performance.
     */
    public void displayResults() {
        // Use HashSet method for duplicate removal (faster)
        int[] result1 = removeDuplicatesWithHashSet();
        System.out.println("\nThe array after removing duplicate elements:");
        // for(int num : result1){
        // System.out.print(num + " ");
        // Start a for loop: initialize counter i at 0, continue as long as i is less than the array length, increment i after each iteration
        for (int i = 0; i < result1.length; i++) {
            System.out.print(result1[i]); // Print the current element of the array result1 at index i (without moving to a new line)
            // Check if the current element is NOT the last one in the array
            if (i < result1.length - 1) { // only print a comma if it's not the last element
                // If not the last element, print a comma right after the value
                System.out.print(",");
            }
        }
    }

//        // Alternative method using nested loops (commented for performance comparison)
//        int[] result2 = removeDuplicatesWithLoop();
//        for (int num : result2) {
//            System.out.print(num + " ");
//        }
}