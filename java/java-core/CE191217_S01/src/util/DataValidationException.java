package util;

/**
 * S01 Manage student
 * Custom exception for data validation errors in the student management system.
 * Extends Exception to create checked exceptions that must be handled.
 * Author: Huynh Kim Hue CE191217 28/11/20255
 */
public class DataValidationException extends Exception {
    /**
     * Constructs a new DataValidationException with the specified detail message.
     *
     * @param message the detail message explaining the validation error
     */
    public DataValidationException(String message) {
        super(message);  // Pass message to parent Exception class
    }
}