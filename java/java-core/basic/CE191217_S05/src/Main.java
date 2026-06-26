import java.util.*; // Import all utility classes including Scanner, List, HashMap

/**
 * S05 Analyze the user input string.
 * Main class for String Analysis Program.
 * Takes user input and displays analysis results for numbers and characters.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Main { // Main class containing the program entry point

    // convert List<Char> sang String
//        private static String listToString(List<Character> list) {
//            StringBuilder sb = new StringBuilder();
//            for (Character c : list) {
//                sb.append(c);
//            }
//            return sb.toString();
//        }

    /**
     * Main method that runs the string analysis program.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) { // Main method - program entry point
        Scanner scanner = new Scanner(System.in);  // Scanner for reading user input

        // Display program header
        System.out.println("===== Analysis String program ====="); // Print program title

        String input = ""; // Variable to store user input
        boolean isValid = false; // Flag to track if input is valid

        // Loop until valid input is received
        while (!isValid) { // Continue looping until valid input is provided
            System.out.print("Input String: "); // Prompt user for input
            input = scanner.nextLine();  // Read the string to analyze

            // Validate input: check if empty or contains only whitespace
            if (input == null || input.trim().isEmpty()) { // Check for null or whitespace-only input
                System.out.println("Error: Input cannot be empty or contain only whitespace!"); // Error message
                System.out.println("Please enter a valid string with at least one non-whitespace character.\n"); // Guidance
            } else {
                isValid = true;  // Valid input received
            }
        }

        AnalysisString analyzer = new AnalysisString();  // Create analyzer instance

        // Analyze the input string
        HashMap<String, List<Integer>> numbers = analyzer.getNumber(input);  // Extract numbers
        HashMap<String, StringBuilder> chars = analyzer.getCharacter(input);  // Extract characters

        // Display analysis results
        System.out.println("\n-----Result Analysis------"); // Section header

        // Display number analysis results in reverse order (as specified)
        System.out.println("Square Numbers: " + numbers.get("Square Numbers")); // Print square numbers
        System.out.println("Odd Numbers: " + numbers.get("Odd Numbers")); // Print odd numbers
        System.out.println("Even Numbers: " + numbers.get("Even Numbers")); // Print even numbers
        System.out.println("All Numbers: " + numbers.get("All Numbers")); // Print all numbers

        // Display character analysis results with StringBuilder
        System.out.println("Uppercase Characters: " + chars.get("Uppercase Characters")); // Print uppercase chars
        System.out.println("Lowercase Characters: " + chars.get("Lowercase Characters")); // Print lowercase chars
        System.out.println("Special Characters: " + chars.get("Special Characters")); // Print special chars
        System.out.println("All Characters: "); // Header for all characters
        System.out.println(chars.get("All Characters"));  // All characters printed separately

        // In ra danh cho List<Char>
//        System.out.println("Uppercase Characters: " + listToString(chars.get("Uppercase Characters")));
//        System.out.println("Lowercase Characters: " + listToString(chars.get("Lowercase Characters")));
//        System.out.println("Special Characters: " + listToString(chars.get("Special Characters")));
//        System.out.println("All Characters: ");
//        System.out.println(listToString(chars.get("All Characters")));
    }
}