package util;

import service.StudentService;
import model.Student;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * S01 Manage student
 * Utility class for handling user input with validation.
 * Provides static methods for reading and validating different types of input.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class InputUtils {
    // Declare a static final Scanner instance for reading user input
    // Shared across all methods to avoid multiple Scanner instances
    private static final Scanner sc = new Scanner(System.in);

    // Declare a static final Map for month abbreviations to numbers
    // Maps month abbreviations (like "Jan") to month numbers (like "01")
    private static final Map<String, String> MONTHS_MAP = new HashMap<>();

    // Static initialization block to populate the months map
    static {
        // Add all month abbreviations with their corresponding two-digit numbers
        MONTHS_MAP.put("Jan", "01"); // Map abbreviation "Jan" to numeric month "01"
        MONTHS_MAP.put("Feb", "02"); // Map abbreviation "Feb" to numeric month "02"
        MONTHS_MAP.put("Mar", "03"); // Map abbreviation "Mar" to numeric month "03"
        MONTHS_MAP.put("Apr", "04"); // Map abbreviation "Apr" to numeric month "04"
        MONTHS_MAP.put("May", "05"); // Map abbreviation "May" to numeric month "05"
        MONTHS_MAP.put("Jun", "06"); // Map abbreviation "Jun" to numeric month "06"
        MONTHS_MAP.put("Jul", "07"); // Map abbreviation "Jul" to numeric month "07"
        MONTHS_MAP.put("Aug", "08"); // Map abbreviation "Aug" to numeric month "08"
        MONTHS_MAP.put("Sep", "09"); // Map abbreviation "Sep" to numeric month "09"
        MONTHS_MAP.put("Oct", "10"); // Map abbreviation "Oct" to numeric month "10"
        MONTHS_MAP.put("Nov", "11"); // Map abbreviation "Nov" to numeric month "11"
        MONTHS_MAP.put("Dec", "12"); // Map abbreviation "Dec" to numeric month "12"
    }

    /**
     * Reads and validates menu choice input from user.
     * Only accepts numbers 1-4 as valid input.
     *
     * @return validated menu choice as integer (1-4)
     */
    public static int inputMenuChoice() {
        // Start infinite loop until valid input is received
        while (true) {
            // Prompt user for menu choice
            System.out.print("Please choose menu (1 - 4): ");
            // Read input, trim whitespace from both ends
            String input = sc.nextLine().trim();
            // Check if input matches pattern: single digit from 1 to 4
            if (input.matches("[1-4]")) {
                // Parse string to integer and return
                return Integer.parseInt(input);
            }
            // Display error message for invalid input
            System.out.println("Invalid choice! Please enter number from 1 to 4.");
        }
    }

    /**
     * Reads and validates student name input.
     * Ensures name is not empty, doesn't contain '#' and contains only letters and spaces
     *
     * @return validated student name
     */
    public static String inputStudentName() {
        // Start infinite loop until valid input is received
        while (true) {
            // Prompt user for student name
            System.out.print("Student name: ");
            // Read input, trim whitespace from both ends
            String name = sc.nextLine().trim();

            // Replace multiple spaces with single space
            name = name.replaceAll("\\s+", " ");

            // Check if name is empty after trimming
            if (name.isEmpty()) {
                // Display error message for empty name
                System.out.println("Name cannot be empty! Try again.");
                // Continue to next iteration of loop
                continue;
            }

            // Check if name contains '#' character (reserved for file format)
            if (name.contains("#")) {
                // Display error message for invalid character
                System.out.println("Cannot contain character '#'! Try again.");
                // Continue to next iteration of loop
                continue;
            }

            // Validate name contains only allowed characters: letters, spaces, apostrophes
            if (!name.matches("^[A-Za-z\\s']+$")) {
                // Display error message for invalid characters
                System.out.println("Name can only contain letters, spaces and apostrophes! Try again.");
                // Continue to next iteration of loop
                continue;
            }
            // Return valid name
            return name;
        }
    }

    /**
     * Reads and validates student code input.
     * Ensures code follows format: 2 letters followed by 3 digits (e.g., SV001)
     * Optionally checks for duplicate codes in the system.
     *
     * @param checkDuplicate if true, checks for duplicate codes using service
     * @param service        the StudentService for duplicate checking (can be null if checkDuplicate is false)
     * @return validated student code
     */
    public static String inputStudentCode(boolean checkDuplicate, StudentService service) {
        // Start infinite loop until valid input is received
        while (true) {
            // Prompt user for student code
            System.out.print("Student code: ");
            // Read input, trim whitespace, convert to uppercase
            String code = sc.nextLine().trim().toUpperCase();

            // Remove all whitespace characters
            code = code.replaceAll("\\s+", "");

            // Check if code is empty after cleaning
            if (code.isEmpty()) {
                // Display error message for empty code
                System.out.println("Code cannot be empty! Try again.");
                // Continue to next iteration of loop
                continue;
            }

            // Check if code contains '#' character (reserved for file format)
            if (code.contains("#")) {
                // Display error message for invalid character
                System.out.println("Cannot contain character '#'! Try again.");
                // Continue to next iteration of loop
                continue;
            }

            // Validate code format: exactly 2 uppercase letters followed by exactly 3 digits
            // ^ = start of string, [A-Z]{2} = exactly 2 uppercase letters
            // \d{3} = exactly 3 digits, $ = end of string
            if (!code.matches("^[A-Z]{2}\\d{3}$")) {
                // Display error message with format example
                System.out.println("Code must be in format: 2 letters followed by 3 digits (e.g., SV001)! Try again.");
                // Continue to next iteration of loop
                continue;
            }

            // Check for duplicate code if requested and service is provided
            if (checkDuplicate && service != null) {
                // Use service method to check if code already exists
                Student existingStudent = service.getStudentByCode(code);
                // If student with same code exists
                if (existingStudent != null) {
                    // Display error message with existing student's name
                    System.out.println("Error: Student code " + code + " already exists for student: " + existingStudent.getName());
                    // Prompt user to enter different code
                    System.out.println("Please enter a different student code.");
                    // Continue to next iteration of loop
                    continue;
                }
            }

            // Return valid code
            return code;
        }
    }

    /**
     * Reads and validates student code input WITHOUT duplicate checking.
     * For backward compatibility.
     *
     * @return validated student code
     */
    public static String inputStudentCode() {
        // Call the main method with duplicate checking disabled
        return inputStudentCode(false, null);
    }

    /**
     * Reads and validates date input in specific format with actual date validation.
     * Expected format: dd-MMM-yyyy (e.g., 11-Jan-1991)
     *
     * @return validated date string
     */
    public static String inputDate() {
        // Start infinite loop until valid input is received
        while (true) {
            // Prompt user for date with format example
            System.out.print("Date of birth (dd-MMM-yyyy): ");
            // Read input, trim whitespace from both ends
            String date = sc.nextLine().trim();

            // Remove all whitespace characters
            date = date.replaceAll("\\s+", "");

            // Validate basic format using regex
            // \d{1,2} = 1 or 2 digits for day
            // [A-Za-z]{3} = exactly 3 letters for month abbreviation
            // \d{4} = exactly 4 digits for year
            if (!date.matches("\\d{1,2}-[A-Za-z]{3}-\\d{4}")) {
                // Display error message with format example
                System.out.println("Invalid format! Example: 11-Sep-2001");
                // Continue to next iteration of loop
                continue;
            }

            // Split date string into parts using hyphen as delimiter
            String[] parts = date.split("-");
            // Extract day part (before first hyphen)
            String dayStr = parts[0];
            // Extract month abbreviation part (middle part)
            String monthAbbr = parts[1];
            // Extract year part (after second hyphen)
            String yearStr = parts[2];

            // Check if month abbreviation exists in months map
            if (!MONTHS_MAP.containsKey(monthAbbr)) {
                // Display error message with valid month abbreviations
                System.out.println("Invalid month abbreviation! Use: Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec");
                // Continue to next iteration of loop
                continue;
            }

            // Try to parse and validate the date
            try {
                // Parse day string to integer
                int day = Integer.parseInt(dayStr);
                // Parse year string to integer
                int year = Integer.parseInt(yearStr);

                // Validate year range
                if (year < 1900 || year > 2025) {
                    // Display error message for invalid year
                    System.out.println("Year must be between 1900 and 2025! Try again.");
                    // Continue to next iteration of loop
                    continue;
                }

                // Get month number from map (e.g., "Jan" -> "01")
                String monthNum = MONTHS_MAP.get(monthAbbr);
                // Create standard date format: dd-MM-yyyy
                String standardDate = String.format("%02d-%s-%04d", day, monthNum, year);

                // Create formatter for parsing date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                // Parse date string to LocalDate object
                LocalDate localDate = LocalDate.parse(standardDate, formatter);

                // Check if date is in the future
                if (localDate.isAfter(LocalDate.now())) {
                    // Display error message for future date
                    System.out.println("Date of birth cannot be in the future! Try again.");
                    // Continue to next iteration of loop
                    continue;
                }

                // Return formatted date with proper month capitalization
                // First letter uppercase, rest lowercase
                return String.format("%02d-%s-%04d",
                        day,
                        monthAbbr.substring(0, 1).toUpperCase() + monthAbbr.substring(1).toLowerCase(),
                        year);

            } catch (NumberFormatException e) {
                // Handle case where day or year is not a valid number
                System.out.println("Invalid day or year! Please enter valid numbers.");
            } catch (DateTimeParseException e) {
                // Handle case where date is invalid (e.g., Feb 30)
                System.out.println("Invalid date! Please check day and month combination.");
            }
        }
    }

    /**
     * Reads and validates learning point input.
     * Ensures point is a valid number between 0.0 and 4.0
     *
     * @return validated learning point as double
     */
    public static double inputPoint() {
        // Start infinite loop until valid input is received
        while (true) {
            // Prompt user for learning point
            System.out.print("Learning point: ");
            // Read input, trim whitespace from both ends
            String input = sc.nextLine().trim();

            // Remove all whitespace characters
            input = input.replaceAll("\\s+", "");

            // Try to parse the input as a double
            try {
                // Parse string to double
                double point = Double.parseDouble(input);
                // Validate point range (0.0 to 4.0 inclusive)
                if (point >= 0 && point <= 4.0) {
                    // Round to 1 decimal place: multiply by 10, round, divide by 10
                    return Math.round(point * 10.0) / 10.0;
                }
                // Display error message for out-of-range point
                System.out.println("Point must be between 0.0 and 4.0!");
            } catch (NumberFormatException e) {
                // Handle case where input is not a valid number
                System.out.println("Invalid number! Please enter a valid point (e.g., 3.5).");
            }
        }
    }

    /**
     * Reads search keyword input from user.
     * Trims and removes extra spaces
     *
     * @return trimmed search keyword
     */
    public static String inputSearchName() {
        // Prompt user for student name to search
        System.out.print("Please enter student name: ");
        // Read input, trim whitespace from both ends
        String name = sc.nextLine().trim();
        // Replace multiple spaces with single space and return
        return name.replaceAll("\\s+", " ");
    }
}