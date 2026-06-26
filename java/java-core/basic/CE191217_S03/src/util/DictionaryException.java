package util;

/**
 * S03 English – English dictionary
 * Custom exception for dictionary-related errors
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class DictionaryException extends Exception {
    /**
     * S03 English – English dictionary
     * Constructor with error message
     * @param message The error message to display
     */
    public DictionaryException(String message) {
        // Pass the message to parent Exception class
        super(message);
    }
}