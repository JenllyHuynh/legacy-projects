package util;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * L01 -Create a Java console program to manage students
 * Utility class for handling and validating student management input.
 * Provides methods for reading various student-related inputs with validation.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class InputUtils {
    // Declare a static Scanner instance for reading input from console
    private static final Scanner scanner = new Scanner(System.in);
    // Declare an array of valid course names (Java, .Net, C/C++)
    private static final String[] VALID_COURSES = {"Java", ".Net", "C/C++"};
    // Declare a Pattern for validating student names (letters and spaces only)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)*$");
    // Declare a Pattern for validating student IDs (SV followed by 3 digits)
    private static final Pattern ID_PATTERN = Pattern.compile("^SV\\d{3}$");

    /**
     * Reads and validates student ID in format SVXXX (where XXX is non-zero number).
     * Validates format, ensures number part is not 000.
     * Example: SV001, SV123 (SV000 is invalid)
     *
     * @return validated student ID in uppercase
     */
    public static String readId() {
        while (true) {
            // Display prompt and read ID input
            System.out.print("Enter Student Code (format SVXXX, e.g., SV001): ");
            // Trim, convert to uppercase for consistency
            String id = scanner.nextLine().trim().toUpperCase();

            // Check for empty input
            if (id.isEmpty()) {
                System.out.println("ID cannot be empty!");
                continue;
            }

            // Validate format using regex pattern
            if (!ID_PATTERN.matcher(id).matches()) {
                System.out.println("Invalid format! Must be SV followed by 3 digits (e.g., SV001).");
                continue;
            }

            // Extract number part (digits after "SV")
            String numberPart = id.substring(2);
            // Check if number part is "000" (invalid)
            if (numberPart.equals("000")) {
                System.out.println("ID cannot be 'SV000'! Must be SV001 to SV999.");
                continue;
            }

            // Return validated ID
            return id;
        }
    }

    /**
     * Reads and validates student name.
     * Name can contain only letters and single spaces between words.
     *
     * @return validated and normalized student name
     */
    public static String readName() {
        while (true) {
            // Display prompt and read name input
            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine().trim();

            // Check for empty input
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty!");
                continue;
            }

            // Replace multiple spaces with single space
            name = name.replaceAll("\\s+", " ");

            // Validate using name pattern (letters and spaces only)
            if (!NAME_PATTERN.matcher(name).matches()) {
                System.out.println("Invalid name! Only letters and single spaces allowed.");
                continue;
            }

            // Return validated name
            return name;
        }
    }

    /**
     * Reads and validates course name.
     * Accepts Java, .Net, or C/C++ (case-insensitive).
     * Returns standardized format.
     *
     * @return validated course name in standardized format
     */
    public static String readCourse() {
        while (true) {
            // Display prompt and read course input
            System.out.print("Enter Course Name (Java, .Net, C/C++): ");
            String course = scanner.nextLine().trim();

            // Check for empty input
            if (course.isEmpty()) {
                System.out.println("Course cannot be empty!");
                continue;
            }

            // Check against valid courses (case-insensitive)
            for (String validCourse : VALID_COURSES) {
                if (validCourse.equalsIgnoreCase(course)) {
                    // Return standardized format
                    return validCourse;
                }
            }

            // Special handling for C/C++ variations
            if (course.equalsIgnoreCase("c++") || course.equalsIgnoreCase("c")) {
                return "C/C++";
            }

            // Invalid course input
            System.out.println("Invalid course! Must be one of: Java, .Net, C/C++");
        }
    }

    /**
     * Asks user if they want to continue (Y/N or YES/NO).
     * Accepts both single letter and full word responses.
     *
     * @return true if user wants to continue, false otherwise
     */
    public static boolean readContinueYN() {
        while (true) {
            // Display prompt and read response
            System.out.print("Do you want to continue (Y/N)? ");
            // Convert to uppercase for case-insensitive comparison
            String input = scanner.nextLine().trim().toUpperCase();

            // Check for Yes response
            if (input.equals("Y") || input.equals("YES")) {
                return true;
            }
            // Check for No response
            else if (input.equals("N") || input.equals("NO")) {
                return false;
            }
            // Invalid response
            else {
                System.out.println("Please enter Y or N.");
            }
        }
    }

    /**
     * Reads search name input for student searching.
     * Accepts full or partial names.
     *
     * @return search name string (trimmed)
     */
    public static String readSearchName() {
        // Display prompt and read search name
        System.out.print("Enter student name or part of name: ");
        // Return trimmed input
        return scanner.nextLine().trim();
    }

    /**
     * Reads student ID for update or delete actions.
     * Less strict validation than readId() for update/delete operations.
     *
     * @return student ID string (trimmed)
     */
    public static String readIdForAction() {
        // Display prompt and read ID
        System.out.print("Enter student ID: ");
        // Return trimmed input
        return scanner.nextLine().trim();
    }

    /**
     * Asks user if they want to update or delete a student.
     * Accepts only 'U' for Update or 'D' for Delete.
     *
     * @return 'U' for Update, 'D' for Delete
     */
    public static char readUD() {
        while (true) {
            // Display prompt and read choice
            System.out.print("Do you want to update (U) or delete (D) student? ");
            // Convert to uppercase for case-insensitive comparison
            String input = scanner.nextLine().trim().toUpperCase();

            // Check for Update choice
            if (input.equals("U")) {
                return 'U';
            }
            // Check for Delete choice
            else if (input.equals("D")) {
                return 'D';
            }
            // Invalid choice
            else {
                System.out.println("Invalid! Enter U or D.");
            }
        }
    }

    /**
     * Reads menu choice from user (1-5).
     * Validates input is a number within valid range.
     *
     * @return validated menu choice (1-5)
     */
    public static int readMenuChoice() {
        while (true) {
            // Display prompt and read choice
            System.out.print("Please choose (1-5): ");
            String input = scanner.nextLine().trim();

            try {
                // Parse input to integer
                int choice = Integer.parseInt(input);
                // Validate choice is within 1-5 range
                if (choice >= 1 && choice <= 5) {
                    return choice;
                } else {
                    System.out.println("Invalid choice! Please select 1-5.");
                }
            } catch (NumberFormatException e) {
                // Handle non-numeric input
                System.out.println("Invalid input! Please enter a number 1-5.");
            }
        }
    }
}