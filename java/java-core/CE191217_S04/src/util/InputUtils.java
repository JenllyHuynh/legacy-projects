package util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * S04 - Write a login function uses MD5 encryption for passwords (separate from FPT Webmail software Project
 * Utility class for handling and validating user input.
 * Provides methods for reading various types of input with validation.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class InputUtils {
    // Declare a static Scanner instance for reading input from console
    private static final Scanner sc = new Scanner(System.in);

    // PHONE: Vietnamese phone number pattern (0/84 + 9 digits)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0|84)\\d{9}$");

    // EMAIL: Strict email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?i)[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"
    );

    // NAME: Allows only letters, whitespace, and apostrophe
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s']+$");

    // USERNAME: Allows letters, numbers, underscore, 3-20 characters
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    /**
     * Reads a non-empty string input from the user.
     * Trims and normalizes whitespace, rejects empty input and '#' character.
     *
     * @param prompt the message to display to the user
     * @return validated non-empty string without '#'
     */
    public static String readNonEmpty(String prompt) {
        while (true) {
            // Display prompt and read input
            System.out.print(prompt);
            // Trim input and replace multiple spaces with single space
            String s = sc.nextLine().trim();
            s = s.replaceAll("\\s+", " ");

            // Check if input is empty
            if (s.isEmpty()) {
                System.out.println("Cannot be empty!");
                continue;
            }

            // Check if input contains forbidden '#' character
            if (s.contains("#")) {
                System.out.println("Cannot contain '#' character!");
                continue;
            }

            // Return validated input
            return s;
        }
    }

    /**
     * Reads and validates a username.
     * Username must be 3-20 characters, containing only letters, numbers, underscore.
     *
     * @return validated username
     */
    public static String readUsername() {
        while (true) {
            // Read non-empty username
            String username = readNonEmpty("Account: ");

            // Validate against username pattern
            if (!USERNAME_PATTERN.matcher(username).matches()) {
                System.out.println("Username must be 3-20 characters, letters/numbers/underscore only!");
                continue;
            }

            // Return validated username
            return username;
        }
    }

    /**
     * Reads a password input (non-empty validation only).
     * Note: Password is read in plain text, hashing should be applied separately.
     *
     * @return non-empty password string
     */
    public static String readPassword() {
        // Use readNonEmpty for basic password validation
        return readNonEmpty("Password: ");
    }

    /**
     * Reads and validates a person's name.
     * Name can only contain letters, spaces, and apostrophes.
     *
     * @return validated name string
     */
    public static String readName() {
        while (true) {
            // Read non-empty name
            String name = readNonEmpty("Name: ");

            // Validate against name pattern
            if (!NAME_PATTERN.matcher(name).matches()) {
                System.out.println("Name can only contain letters, spaces and apostrophes!");
                continue;
            }
            // Return validated name
            return name;
        }
    }

    /**
     * Reads and validates a Vietnamese phone number.
     * Must start with 0 or 84, followed by valid prefix 9 digits.
     *
     * @return validated phone number
     */
    public static String readPhone() {
        while (true) {
            // Display prompt and read phone input
            System.out.print("Phone: ");
            // Trim and remove all whitespace from phone number
            String phone = sc.nextLine().trim().replaceAll("\\s+", "");

            // Check empty input
            if (phone.isEmpty()) {
                System.out.println("Phone cannot be empty!");
                continue;
            }

            // Check for forbidden '#' character
            if (phone.contains("#")) {
                System.out.println("Phone cannot contain '#'!");
                continue;
            }

            // Validate against Vietnamese phone pattern
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println("Invalid Vietnamese phone number! Must start with 0/84 and 10-11 digits");
                System.out.println("Example: 0912345678 or 84912345678");
                continue;
            }

            // Return validated phone number
            return phone;
        }
    }

    /**
     * Reads and validates an email address.
     * Uses strict email regex pattern validation.
     *
     * @return validated email address
     */
    public static String readEmail() {
        while (true) {
            // Display prompt and read email input
            System.out.print("Email: ");
            String email = sc.nextLine().trim();

            // Check empty input
            if (email.isEmpty()) {
                System.out.println("Email cannot be empty!");
                continue;
            }

            // Check for forbidden '#' character
            if (email.contains("#")) {
                System.out.println("Email cannot contain '#'!");
                continue;
            }

            // Validate against email pattern
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("Invalid email format! Example: user@domain.com");
                continue;
            }

            // Additional check: email should not end with dot
            if (email.endsWith(".")) {
                System.out.println("Email cannot end with a dot!");
                continue;
            }

            // Return validated email
            return email;
        }
    }

    /**
     * Reads an address input with basic validation.
     * Uses readNonEmpty method for validation.
     *
     * @return validated address string
     */
    public static String readAddress() {
        // Use readNonEmpty for address validation
        return readNonEmpty("Address: ");
    }

    /**
     * Reads and validates a Date of Birth input.
     * Validates format, leap years, age restrictions, and real date existence.
     * Format must be "dd/MM/yyyy", age must be between 1 and 100 years.
     *
     * @return validated date of birth string in "dd/MM/yyyy" format
     */
    public static String readDOB() {
        while (true) {
            // Display prompt and read date input
            System.out.print("DOB (dd/MM/yyyy): ");
            // Trim and remove all whitespace from date input
            String dob = sc.nextLine().trim().replaceAll("\\s+", "");

            // Check for forbidden '#' character in date input
            if (dob.contains("#")) {
                System.out.println("Date cannot contain '#'!");
                continue;
            }

            // Validate date format using regex
            // Pattern checks: digits for day (1-2), slash, digits for month (1-2), slash, 4 digits for year
            if (!dob.matches("^\\d{1,2}/\\d{1,2}/\\d{4}$")) {
                System.out.println("Invalid format! Use dd/MM/yyyy (e.g., 15/03/1990)");
                continue;
            }

            try {
                // Split date string into day, month, and year components
                String[] parts = dob.split("/");
                // Parse day part to integer
                int day = Integer.parseInt(parts[0]);
                // Parse month part to integer
                int month = Integer.parseInt(parts[1]);
                // Parse year part to integer
                int year = Integer.parseInt(parts[2]);

                // Create LocalDate object from parsed components
                LocalDate date = LocalDate.of(year, month, day);

                // Manual leap year calculation (divisible by 400, OR divisible by 4 but not by 100)
                boolean isLeapManual = (year % 400 == 0) ||
                        (year % 4 == 0 && year % 100 != 0);
                // Java built-in leap year calculation
                boolean isLeapJava = date.isLeapYear();

                // Compare manual and Java calculations - they should match
                if (isLeapManual != isLeapJava) {
                    System.err.println("Error: Leap year calculation mismatch!");
                    continue;
                }

                // Check if date is in the future (invalid for date of birth)
                if (date.isAfter(LocalDate.now())) {
                    System.out.println("Date of birth cannot be in the future!");
                    continue;
                }

//                // Calculate minimum age (1-year-old) threshold
//                LocalDate minAge = LocalDate.now().minusYears(1);
//                // Check if person is at least 1-year-old
//                if (date.isAfter(minAge)) {
//                    System.out.println("You must be at least 1 years old!");
//                    continue;
//                }

                // Calculate maximum age (100 years old) threshold
                LocalDate maxAge = LocalDate.now().minusYears(100);
                // Check if person is not older than 100 years
                if (date.isBefore(maxAge)) {
                    System.out.println("Please enter a valid date (not older than 100 years)!");
                    continue;
                }

                // Return validated date string
                return dob;

            } catch (NumberFormatException e) {
                // Handle non-numeric day/month/year input
                System.out.println("Day, month and year must be numbers!");
                continue;
            } catch (DateTimeException e) {
                // Handle invalid date (e.g., February 30, April 31, etc.)
                // Get the detailed error message
                String errorMsg = e.getMessage();

                // Display user-friendly error messages based on exception type
                if (errorMsg != null && errorMsg.contains("not a leap year")) {
                    // Special case: February 29 in non-leap year
                    System.out.println("Invalid date! February 29 only exists in leap years.");
                } else if (errorMsg != null && errorMsg.contains("Invalid value")) {
                    // General invalid date (e.g., day 32, month 13)
                    System.out.println("Invalid date! Please check day, month, and year.");
                } else {
                    // Other date format errors
                    System.out.println("Invalid date! Use format dd/MM/yyyy (e.g., 15/03/1990)");
                }
                continue;
            } catch (ArrayIndexOutOfBoundsException e) {
                // Handle malformed date string (wrong number of slashes)
                System.out.println("Invalid format! Use dd/MM/yyyy");
                continue;
            }
        }
    }

    /**
     * Reads a numeric choice input (1, 2, or 3).
     * Validates input is a number and within valid range.
     *
     * @return validated choice (1, 2, or 3)
     */
    public static int readChoice() {
        while (true) {
            // Display prompt and read choice input
            System.out.print("Please choice one option: ");
            String input = sc.nextLine().trim();

            // Check for forbidden '#' character
            if (input.contains("#")) {
                System.out.println("Cannot contain '#' character!");
                continue;
            }

            try {
                // Parse input to integer
                int choice = Integer.parseInt(input);
                // Validate choice is 1, 2, or 3
                if (choice >= 1 && choice <= 3) return choice;
                System.out.println("Please enter 1, 2 or 3!");
            } catch (NumberFormatException e) {
                // Handle non-numeric input
                System.out.println("Invalid input! Enter a number.");
            }
        }
    }

    /**
     * Asks user if they want to change password.
     * Accepts only 'Y' or 'N' (case-insensitive).
     *
     * @return true if user answers 'Y', false if 'N'
     */
    public static boolean askChangePassword() {
        while (true) {
            // Display prompt and read answer
            System.out.print("do you want change password now? Y/N: ");
            // Convert to uppercase for case-insensitive comparison
            String ans = sc.nextLine().trim().toUpperCase();

            // Check for forbidden '#' character
            if (ans.contains("#")) {
                System.out.println("Cannot contain '#' character!");
                continue;
            }

            // Check for 'Y' (Yes) response
            if (ans.equals("Y")) return true;
            // Check for 'N' (No) response
            if (ans.equals("N")) return false;
            // Handle invalid response
            System.out.println("Please enter Y or N!");
        }
    }
}