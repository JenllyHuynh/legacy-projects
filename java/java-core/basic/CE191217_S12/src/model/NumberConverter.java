package model;

/**
 * S12 - Convert binary, octal and hexadecimal to decimal
 * Model class for number conversion operations.
 * Converts numbers from various bases (binary, octal, hexadecimal) to decimal.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class NumberConverter {
    // Declare private final String to store the input number
    private final String input;
    // Declare private final int to store the original base (2, 8, or 16)
    private final int base;
    // Declare private final long to store the converted decimal value
    private final long decimal;

    /**
     * Constructs a NumberConverter with the specified input and base.
     * Automatically converts the input to decimal upon creation.
     *
     * @param input the number string to convert
     * @param base  the base of the input number (2, 8, or 16)
     */
    public NumberConverter(String input, int base) {
        // Clean input: trim whitespace and convert to uppercase
        this.input = input.trim().toUpperCase();
        // Store the original base
        this.base = base;
        // Convert the input to decimal and store result
        this.decimal = convert();
    }

    /**
     * Converts the input number from its original base to decimal.
     * Uses manual algorithm to process each digit from left to right.
     *
     * @return decimal representation of the input number
     */
    private long convert() {
        // Store the input number string for processing
        String digits = this.input;
        // Initialize result accumulator to 0
        long result = 0;

        // Process each character in the input string from left to right
        for (int i = 0; i < digits.length(); i++) {

            // Get the character at current position
            char c = digits.charAt(i);
            // Convert character to its numeric value based on base
            int digitValue = charToValue(c);

            // Multiply current result by base and add new digit value
            // This is the standard algorithm: result = result * base + digit
            result = result * base + digitValue;
        }
        // Return the final decimal result
        return result;
    }

    /**
     * Converts a character to its numeric value based on the current base.
     * Supports digits 0-9 and letters A-F for hexadecimal.
     *
     * @param c the character to convert
     * @return numeric value of the character
     * @throws IllegalArgumentException if character is invalid for the current base
     */
    private int charToValue(char c) {
        // Check if character is a digit (0-9)
        if (c >= '0' && c <= '9') {
            // Convert ASCII digit to integer value
            // '0' - '0' = 0, '1' - '0' = 1, ..., '9' - '0' = 9
            return c - '0';
        }
        // Check if character is a hexadecimal letter (A-F)
        else if (c >= 'A' && c <= 'F') {
            // Convert ASCII letter to integer value for base 16
            // 'A' -> 10 + (65 - 65) = 10
            return 10 + (c - 'A');
        }
        // Invalid character for supported bases
        else {
            // Throw exception with descriptive message
            throw new IllegalArgumentException(
                    "Invalid character '" + c + "' for base " + base
            );
        }
    }

    /**
     * Retrieves the original input number as a string.
     *
     * @return the input number string
     */
    public String getInput() {
        // Return the input value
        return input;
    }

    /**
     * Retrieves the numerical base of the original input number.
     *
     * @return the base of the input number
     */
    public int getBase() {
        // Return the base value
        return base;
    }

    /**
     * Retrieves the decimal (base-10) representation of the original input number.
     *
     * @return the decimal value of the input number
     */
    public long getDecimal() {
        // Return the decimal value
        return decimal;
    }

    /**
     * Returns a formatted string representation of the conversion result.
     *
     * @return string showing the decimal conversion result
     */
    @Override
    public String toString() {
        // Return formatted result message
        return "Decimal number is: " + decimal;
    }
}