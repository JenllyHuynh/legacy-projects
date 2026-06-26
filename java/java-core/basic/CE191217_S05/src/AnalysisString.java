import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * S05 Analyze the user input string.
 * String analysis utility class.
 * Provides methods to extract and categorize numbers and characters from input strings.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class AnalysisString {
    /**
     * Extracts and categorizes all numbers found in the input string.
     * Categorizes numbers into: all numbers, even numbers, odd numbers, and square numbers.
     *
     * @param input the string to analyze for numbers
     * @return HashMap with four categories as keys and lists of numbers as values
     */
    public HashMap<String, List<Integer>> getNumber(String input) {
        // Initialize result map with empty lists for each category
        HashMap<String, List<Integer>> result = new HashMap<>(); // Create new HashMap instance
        result.put("All Numbers", new ArrayList<>()); // Add key "All Numbers" with empty ArrayList
        result.put("Even Numbers", new ArrayList<>()); // Add key "Even Numbers" with empty ArrayList
        result.put("Odd Numbers", new ArrayList<>()); // Add key "Odd Numbers" with empty ArrayList
        result.put("Square Numbers", new ArrayList<>()); // Add key "Square Numbers" with empty ArrayList

        // Handle null or empty input by returning empty lists
        if (input == null || input.isEmpty()) return result;

        // Pattern to match sequences of digits (\\d+ means one or more digits)
        Pattern p = Pattern.compile("\\d+"); // Compile regex pattern to find digit sequences
        Matcher m = p.matcher(input);  // Create matcher for the input string

        // Find all number sequences in the input
        while (m.find()) { // Loop while pattern matches are found in input
            String numStr = m.group();  // Extract the matched number string
            int num = Integer.parseInt(numStr);  // Convert to integer

            // Add to "All Numbers" category
            result.get("All Numbers").add(num); // Add number to "All Numbers" list

            // Categorize as even or odd
            if (num % 2 == 0) { // Check if number is divisible by 2
                result.get("Even Numbers").add(num); // Add to even numbers list
            } else { // If not divisible by 2
                result.get("Odd Numbers").add(num); // Add to odd numbers list
            }
            // Check if number is a perfect square
            int sqrt = (int) Math.sqrt(num); // Calculate integer square root
            if (sqrt * sqrt == num) { // Check if square of sqrt equals original number
                result.get("Square Numbers").add(num); // Add to square numbers list
            }
        }
        return result; // Return the populated HashMap
    }

    /**
     * Extracts and categorizes characters from the input string.
     * Categorizes characters into: all characters, uppercase, lowercase, and special characters.
     *
     * @param input the string to analyze for characters
     * @return HashMap with four categories as keys and StringBuilder of characters as values
     */
    public HashMap<String, StringBuilder> getCharacter(String input) {
        HashMap<String, StringBuilder> result = new HashMap<>(); // Create HashMap for results

        //HashMap<String, List<Character>> result = new HashMap<>();
//        result.put("All Characters", new ArrayList<>());
//        result.put("Uppercase Characters", new ArrayList<>());
//        result.put("Lowercase Characters", new ArrayList<>());
//        result.put("Special Characters", new ArrayList<>());
//        if (input == null || input.isEmpty()) return result;

        // Initialize StringBuilders for each character category
        StringBuilder allChars = new StringBuilder(); // StringBuilder for all characters
        StringBuilder upper = new StringBuilder(); // StringBuilder for uppercase characters
        StringBuilder lower = new StringBuilder(); // StringBuilder for lowercase characters
        StringBuilder special = new StringBuilder(); // StringBuilder for special characters

        // Handle null or empty input
        if (input == null || input.isEmpty()) { // Check for invalid input
            // Return empty results for all categories
            result.put("All Characters", allChars); // Add empty StringBuilder for all characters
            result.put("Uppercase Characters", upper); // Add empty StringBuilder for uppercase
            result.put("Lowercase Characters", lower); // Add empty StringBuilder for lowercase
            result.put("Special Characters", special); // Add empty StringBuilder for special
            return result; // Return empty result map
        }

        // Process each character in the input string
        for (char c : input.toCharArray()) {
            allChars.append(c);  // Add to "All Characters" category
            //result.get("All Characters").add(c);

            // Categorize based on character type
            if (Character.isUpperCase(c)) {
                upper.append(c);  // Uppercase letter
                //result.get("Uppercase Characters").add(c);
            } else if (Character.isLowerCase(c)) {
                lower.append(c);  // Lowercase letter
                //result.get("Lowercase Characters").add(c);
            } else if (!Character.isWhitespace(c)) {
                special.append(c);  // Special character (non-letter, non-whitespace)
                //result.get("Special Characters").add(c);
            }
        }
        //List<Char>
        //return result;

        // Store results in the HashMap
        result.put("All Characters", allChars); // Add all characters StringBuilder to map
        result.put("Uppercase Characters", upper); // Add uppercase StringBuilder to map
        result.put("Lowercase Characters", lower); // Add lowercase StringBuilder to map
        result.put("Special Characters", special); // Add special characters StringBuilder to map
        return result; // Return populated HashMap
    }
}