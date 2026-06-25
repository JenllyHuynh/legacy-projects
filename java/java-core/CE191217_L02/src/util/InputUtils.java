package util;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * L02 - Create a Java console program to manage Candidates
 * Utility class for handling and validating all candidate input
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class InputUtils {
    // Declare a static Scanner instance for reading input from console
    private static final Scanner scanner = new Scanner(System.in);

    // Declare a constant for current year to validate birthdates
    private static final int CURRENT_YEAR = java.time.Year.now().getValue();

    /**
     * NAME_PATTERN: Validates first name and last name
     * Rules: Only letters and single spaces between words
     * Examples: "John", "Van A", "Nguyen Van A"
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)*$");

    /**
     * EMAIL_PATTERN: Validates email address
     * Format: <account name>@<domain> (e.g.: khanhvh@fe.edu.vn)
     * Rules:
     * - Account: letters, numbers, dots, underscores, hyphens, plus signs
     * - Domain: must contain at least one dot
     * - TLD: at least 2 letters
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /**
     * PHONE_PATTERN: Validates phone number
     * Requirements: "is number with minimum 10 characters"
     * Rules: Only digits, minimum 10 characters
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0|84)\\d{9,}$");

    /**
     * YEAR_PATTERN: Validates year format
     * Requirements: "is number with length is 4 character (1900...Current Year)"
     * Rules: Exactly 4 digits
     */
    private static final Pattern YEAR_PATTERN = Pattern.compile("^\\d{4}$");

    /**
     * DATE_PATTERN: Validates graduation date format
     * Format: YYYY or YYYY-MM
     * Examples: "2020", "2020-06"
     */
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}(-\\d{2})?$");

    /**
     * VALID_RANKS: Valid graduation rank values
     * Requirements: "with one of 4 values (Excellence, Good, Fair, Poor)"
     */
    private static final String[] VALID_RANKS = {"Excellence", "Good", "Fair", "Poor"};

    /**
     * ID_PATTERN: Validates an identifier format.
     * Format: Two letters followed by exactly three digits.
     * Example: "AB123", "xy001".
     */
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z]{2}\\d{3}$");

    /**
     * SEMESTER_PATTERN: Validates and matches semester format.
     * Format: Season (Fall, Summer, or Spring) followed by two digits for the year.
     * The validation is case-insensitive.
     * Examples: "Fall24", "summer23".
     */
    private static final Pattern SEMESTER_PATTERN = Pattern.compile("^(Fall|Summer|Spring)(\\d{2})$", Pattern.CASE_INSENSITIVE);

    /**
     * Reads and validates a non-empty string input
     *
     * @param prompt The prompt message to display
     * @return Validated non-empty string
     */
    private static String readNonEmptyString(String prompt) {
        while (true) {
            // Display prompt and read input
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            // Check if input is empty
            if (input.isEmpty()) {
                System.out.println("Error: This field cannot be empty!");
                continue;
            }
            // Return validated input
            return input;
        }
    }

    /**
     * Reads and validates a string with regex pattern
     *
     * @param prompt       The prompt message to display
     * @param pattern      The regex pattern to validate against
     * @param errorMessage Error message if validation fails
     * @return Validated string matching the pattern
     */
    private static String readStringWithPattern(String prompt, Pattern pattern, String errorMessage) {
        while (true) {
            // Read non-empty string
            String input = readNonEmptyString(prompt);

            // Validate against pattern
            if (!pattern.matcher(input).matches()) {
                System.out.println("Error: " + errorMessage);
                continue;
            }

            // Return validated input
            return input;
        }
    }

    /**
     * Reads and validates an integer within a specified range
     *
     * @param prompt The prompt message to display (should include range info)
     * @param min    Minimum allowed value (inclusive)
     * @param max    Maximum allowed value (inclusive)
     * @return Validated integer within the specified range
     */
    private static int readIntegerInRange(String prompt, int min, int max) {
        while (true) {
            // Display prompt and read input
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                // Parse input to integer
                int value = Integer.parseInt(input);

                // Validate range
                if (value < min || value > max) {
                    System.out.println("Error: Value must be between " + min + " and " + max + "!");
                    continue;
                }

                // Return validated value
                return value;

            } catch (NumberFormatException e) {
                // Handle non-numeric input
                System.out.println("Error: Please enter a valid number!");
            }
        }
    }

    /**
     * Reads and validates Candidate ID
     * Note: Duplicate ID check is handled in Service layer
     *
     * @return Validated candidate ID
     */
    public static String readCandidateId() {
        // Use readStringWithPattern for basic validation
        return readStringWithPattern(
                "Enter Candidate ID: ",
                ID_PATTERN,
                "ID must start with 2 character, followed by 3 digits! Example: SV001 Example: SV001"
        );
    }

    /**
     * Reads and validates First Name
     * Rules: Only letters and spaces allowed
     *
     * @return Validated first name
     */
    public static String readFirstName() {
        // Use NAME_PATTERN for validation
        return readStringWithPattern(
                "Enter First Name: ", NAME_PATTERN, "First name can only contain letters and spaces!"
        );
    }

    /**
     * Reads and validates Last Name
     * Rules: Only letters and spaces allowed
     *
     * @return Validated last name
     */
    public static String readLastName() {
        // Use NAME_PATTERN for validation
        return readStringWithPattern(
                "Enter Last Name: ", NAME_PATTERN, "Last name can only contain letters and spaces!"
        );
    }

    /**
     * Reads and validates Birth Date
     * Requirements: "is number with length is 4 character (1900...Current Year)"
     *
     * @return Validated birth year (YYYY format)
     */
    public static String readBirthDate() {
        while (true) {
            // Read year with pattern validation
            String year = readStringWithPattern(
                    "Enter Birth Year (YYYY, 1900-" + CURRENT_YEAR + "): ",
                    YEAR_PATTERN,
                    "Birth year must be exactly 4 digits (YYYY)!"
            );

            // Parse year to integer for range validation
            int yearValue = Integer.parseInt(year);

            // Check if year is within valid range: 1900 to current year
            if (yearValue < 1900) {
                // Display error and skip to the next iteration of the loop.
                System.out.println("Error: Birth year cannot be before 1900!");
                continue;
            }

            // Check if the provided birth year is a future year.
            if (yearValue > CURRENT_YEAR) {
                // Display error and skip to the next iteration of the loop.
                System.out.println("Error: Birth year cannot be in the future!");
                continue;
            }

            // Return validated birth year
            return year;
        }
    }

    /**
     * Reads and validates Address
     *
     * @return Validated address (non-empty)
     */
    public static String readAddress() {
        // Use readNonEmptyString for basic validation
        return readNonEmptyString("Enter Address: ");
    }

    /**
     * Reads and validates Phone Number
     * Requirements: "is number with minimum 10 characters"
     *
     * @return Validated phone number (digits only, minimum 10)
     */
    public static String readPhone() {
        while (true) {
            // Read phone number
            String phone = readNonEmptyString("Enter Phone Number: ");

            // Remove any non-digit characters (spaces, dashes, parentheses, etc.)
            String digitsOnly = phone.replaceAll("[^\\d]", "");

            // Check minimum length requirement
            if (digitsOnly.length() < 10) {
                System.out.println("Error: Phone number must have at least 10 digits!");
                System.out.println("       Current length: " + digitsOnly.length() + " digits");
                continue;
            }

            // Final validation using pattern
            if (!PHONE_PATTERN.matcher(digitsOnly).matches()) {
                System.out.println("Error: Phone number must contain only digits (0-9)! And must sart with: 0 or 84.");
                continue;
            }

            // Return validated phone number
            return digitsOnly;
        }
    }

    /**
     * Reads and validates Email Address
     * Requirements: "with format <account name>@<domain>. (eg: khanhvh@fe.edu.vn)"
     *
     * @return Validated email address
     */
    public static String readEmail() {
        while (true) {
            // Read email with example
            String email = readNonEmptyString("Enter Email (e.g.: khanhvh@fe.edu.vn): ");

            // Basic manual checks before regex for better error messages
            if (!email.contains("@")) {
                System.out.println("Error: Email must contain '@' character!");
                System.out.println("       Example: khanhvh@fe.edu.vn");
                continue;
            }

            // Split email into parts
            String[] parts = email.split("@");
            if (parts.length != 2) {
                System.out.println("Error: Email must contain exactly one '@' character!");
                continue;
            }

            // Extract account and domain parts
            String account = parts[0]; // Previous part @
            String domain = parts[1]; // Next part @

            // Validate account part
            if (account.isEmpty()) {
                System.out.println("Error: Account name (before '@') cannot be empty!");
                continue;
            }

            // Validate domain part
            if (domain.isEmpty()) {
                System.out.println("Error: Domain (after '@') cannot be empty!");
                continue;
            }

            // Check domain contains dot
            if (!domain.contains(".")) {
                System.out.println("Error: Domain must contain at least one dot (.)!");
                System.out.println("       Example: fe.edu.vn");
                continue;
            }

            // Final regex validation
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("Error: Invalid email format!");
                System.out.println("       Valid format: account@domain.example");
                System.out.println("       Example: khanhvh@fe.edu.vn");
                continue;
            }
            // Return validated email
            return email;
        }
    }

    /**
     * Reads and validates Years of Experience
     * Requirements: "is number from 0 to 100"
     *
     * @return Validated years of experience (0-100)
     */
    public static int readExperienceYears() {
        // Use readIntegerInRange with 0-100 limits
        return readIntegerInRange(
                "Enter Years of Experience (0-100): ", 0, 100);
    }

    /**
     * Reads and validates Professional Skill
     *
     * @return Validated professional skill (non-empty)
     */
    public static String readProSkill() {
        // Use readStringWithPattern for basic validation
        return readStringWithPattern(
                "Enter Professional Skill (letters and spaces only): ",
                NAME_PATTERN,  // ^[a-zA-Z]+(\s[a-zA-Z]+)*$
                "Professional skill can only contain letters and spaces!"
        );
    }

    /**
     * Reads and validates Graduation Date input.
     * Validates format (YYYY or YYYY-MM), year range (1900 to current year),
     * and month validity (if YYYY-MM format).
     *
     * @return validated graduation date string in "YYYY" or "YYYY-MM" format
     */
    public static String readGraduationDate() {
        while (true) {
            // Read date input with pattern validation
            String date = readStringWithPattern(
                    "Enter Graduation Date (YYYY or YYYY-MM): ",
                    DATE_PATTERN,
                    "Invalid date format! Use YYYY or YYYY-MM"
            );

            // Extract year part (before dash if exists, otherwise whole string)
            String yearStr = date.split("-")[0];
            // Convert year string to integer for validation
            int year = Integer.parseInt(yearStr);

            // VALIDATION 1: Check if year is not in the future
            if (year > CURRENT_YEAR) {
                System.out.println("Error: Graduation year cannot be in the future!");
                System.out.println("       Maximum allowed: " + CURRENT_YEAR);
                continue;
            }

            // VALIDATION 2: Check if year is not too old (before 1900)
            if (year < 1900) {
                System.out.println("Error: Graduation year cannot be before 1900!");
                continue;
            }

            // If date contains month part (YYYY-MM format)
            if (date.contains("-")) {
                // Extract month part (after dash)
                String monthStr = date.split("-")[1];
                // Convert month string to integer
                int month = Integer.parseInt(monthStr);

                // VALIDATION 3: Check if month is valid (1-12)
                if (month < 1 || month > 12) {
                    System.out.println("Error: Month must be between 01 and 12!");
                    continue;
                }
            }

            // Return validated graduation date
            return date;
        }
    }

    /**
     * Reads and validates Graduation Rank
     * Requirements: "with one of 4 values (Excellence, Good, Fair, Poor)"
     * Case-insensitive input, returns standardized format
     *
     * @return Validated graduation rank
     */
    public static String readGraduationRank() {
        while (true) {
            // Read rank with valid options in prompt
            String rank = readNonEmptyString(
                    "Enter Graduation Rank (Excellence/Good/Fair/Poor): "
            );

            // Case-insensitive comparison with valid ranks
            for (String validRank : VALID_RANKS) {
                if (validRank.equalsIgnoreCase(rank)) {
                    // Return in standardized format
                    return validRank;
                }
            }

            // If no match found, show error with valid options
            System.out.println("Error: Invalid graduation rank!");
            System.out.println("       Valid options are: " + String.join(", ", VALID_RANKS));
        }
    }

    /**
     * Reads and validates University Name
     *
     * @return Validated university name (non-empty)
     */
    public static String readUniversity() {
        // Use readStringWithPattern for basic validation
        return readStringWithPattern("Enter University Name: ", NAME_PATTERN ,"University name can only contain letters and spaces!");
    }

    /**
     * Reads and validates Majors
     *
     * @return Validated majors (non-empty)
     */
    public static String readMajors() {
        // Use readStringWithPattern for basic validation
        return readStringWithPattern("Enter Majors: ", NAME_PATTERN , "Majors can only contain letters and spaces!");
    }

    /**
     * Reads and validates Semester input.
     * Validates format: Season (Fall/Summer/Spring) followed by 2-digit year.
     * Returns standardized format with proper capitalization.
     *
     * @return validated and standardized semester string
     */
    public static String readSemester() {
        while (true) {
            // Display prompt and read semester input
            System.out.print("Enter Semester (FallXX/SummerXX/SpringXX): ");
            String semester = scanner.nextLine().trim();

            // Check for empty input
            if (semester.isEmpty()) {
                System.out.println("Error: Semester cannot be empty!");
                continue;
            }

            // Validate format using regex pattern:
            // ^(Fall|Summer|Spring) - Season must be Fall, Summer, or Spring (case-sensitive)
            // \\d{2}$ - Must end with exactly 2 digits (year)
            if (!Pattern.matches("^(fall|summer|spring)\\d{2}$", semester)) {
                System.out.println("Error: Invalid semester format!");
                // Display helpful examples
                System.out.println("       Please enter FallXX, SummerXX, or SpringXX");
                System.out.println("       Example: Fall24, Summer23, Spring25");
                continue;
            }

            // Extract season part (all characters except last 2)
            String season = semester.substring(0, semester.length() - 2);
            // Extract year part (last 2 characters)
            String year = semester.substring(semester.length() - 2);

            // Declare variable to store the final year value
            int yearNum;

            try {
                // Convert input string 'year' to integer
                int shortYear = Integer.parseInt(year);

                // Convert to full year format: e.g., 24 -> 2024, 23 -> 2023
                if (shortYear >= 0 && shortYear <= 99) {
                    // Add 2000 to make it a 4-digit year
                    yearNum = 2000 + shortYear;
                } else {
                    // Print error if not in range 0–99
                    System.out.println("Error: Invalid year format!");
                    // Skip current loop iteration
                    continue;
                }
            } catch (NumberFormatException e) {
                // Print error if input is not numeric
                System.out.println("Error: Year must be numbers!");
                // Skip current loop iteration
                continue;
            }

            // Validate that year is within allowed range
            if (yearNum < 2020 || yearNum > 2025) {
                // Print error if year is out of range
                System.out.println("Error: Year must be between 2020 and 2025!");
                // Show both parsed and original input
                System.out.println("       Your input: " + yearNum + " (from " + year + ")");
                // Skip current loop iteration
                continue;
            }

            // Standardize format:
            // - First letter of season in uppercase
            // - Remaining letters of season in lowercase
            // - Keep year digits as is
            String standardized = season.substring(0, 1).toUpperCase() +
                    season.substring(1).toLowerCase() +
                    year;

            // Return standardized semester string
            return standardized;
        }
    }

    /**
     * Reads and validates Candidate Type for search
     * 0: Experience, 1: Fresher, 2: Intern
     *
     * @return Validated candidate type (0, 1, or 2)
     */
    public static int readCandidateType() {
        // Use readIntegerInRange with 0-2 limits
        return readIntegerInRange(
                "Enter Candidate Type (0=Experience, 1=Fresher, 2=Intern): ", 0, 2);
    }

    /**
     * Reads search name input (for searching candidates)
     * Accepts partial names for search
     *
     * @return Search name string
     */
    public static String readSearchName() {
        // Display prompt and read search name
        System.out.print("Enter candidate name (First name or Last name): ");
        return scanner.nextLine().trim();
    }


    /**
     * Asks user if they want to continue (Y/N)
     * Accepts both single letter and full word responses (case-insensitive)
     *
     * @return true if user wants to continue, false otherwise
     */
    public static boolean readContinueYN() {
        while (true) {
            // Display prompt and read response
            System.out.print("Do you want to continue (Y/N)? ");
            String input = scanner.nextLine().trim().toUpperCase();

            // Check for Yes response
            if (input.equals("Y") || input.equals("YES")) {
                return true;
            }

            // Check for No response
            if (input.equals("N") || input.equals("NO")) {
                return false;
            }

            // Handle invalid response
            System.out.println("Error: Please enter Y (Yes) or N (No)!");
        }
    }

    /**
     * Reads and validates main menu choice
     * Valid choices: 1 to 5
     *
     * @return Validated menu choice
     */
    public static int readMenuChoice() {
        // Use readIntegerInRange with 1-5 limits
        return readIntegerInRange(
                "Please choose (1-5): ", 1, 5);
    }
}