package service;

import model.NumberConverter;

import java.util.regex.Pattern;

/**
 * S12 - Convert binary, octal and hexadecimal to decimal
 * Service class for number conversion operations.
 * Validates input and performs base conversions.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class ConverterService {
    // Declare static Pattern for validating binary numbers (only 0 and 1)
    private static final Pattern BINARY_PATTERN = Pattern.compile("^[01]+$");
    // Declare static Pattern for validating octal numbers (only digits 0-7)
    private static final Pattern OCTAL_PATTERN = Pattern.compile("^[0-7]+$");
    // Declare static Pattern for validating hexadecimal numbers (0-9, A-F, case-insensitive)
    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9A-Fa-f]+$");

    /**
     * Converts a number from the specified base to decimal.
     * Validates input format before conversion.
     *
     * @param input the number string to convert
     * @param type  the conversion type (1: Binary, 2: Octal, 3: Hexadecimal)
     * @return NumberConverter object containing the conversion result
     * @throws IllegalArgumentException if input is empty, invalid, or type is unsupported
     */
    public NumberConverter convert(String input, int type) {
        // Clean input by trimming whitespace
        String cleaned = input.trim();
        // Check if input is empty
        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty!");
        }

        // Use switch expression to handle different conversion types
        return switch (type) {
            case 1 -> { // Binary conversion
                // Validate input matches binary pattern
                if (!BINARY_PATTERN.matcher(cleaned).matches()) {
                    throw new IllegalArgumentException("Invalid binary number! Only 0 and 1 allowed.");
                }
                // Create and return NumberConverter for binary
                yield new NumberConverter(cleaned, 2);
            }
            case 2 -> { // Octal conversion
                // Validate input matches octal pattern
                if (!OCTAL_PATTERN.matcher(cleaned).matches()) {
                    throw new IllegalArgumentException("Invalid octal number! Only digits 0-7 allowed.");
                }
                // Create and return NumberConverter for octal
                yield new NumberConverter(cleaned, 8);
            }
            case 3 -> { // Hexadecimal conversion
                // Validate input matches hexadecimal pattern
                if (!HEX_PATTERN.matcher(cleaned).matches()) {
                    throw new IllegalArgumentException("Invalid hexadecimal number! Only 0-9 and A-F allowed.");
                }
                // Create and return NumberConverter for hexadecimal
                yield new NumberConverter(cleaned, 16);
            }
            // Throw exception
            default -> throw new IllegalArgumentException("Invalid option!");
        };
    }
}