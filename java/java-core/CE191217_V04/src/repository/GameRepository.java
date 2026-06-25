package repository;

import java.io.*;
import java.util.*;

/**
 * S04 - Simple Slot Machine
 * Repository class for handling game data persistence.
 * Manages saving and loading player's money to/from a file.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class GameRepository { // Main repository class for game data storage
    // Constant for the save file name
    private static final String SAVE_FILE = "slot_save.txt"; // Constant file name for save data

    /**
     * Saves the player's current money to a file.
     *
     * @param moneyInDollars the amount of money to save (in dollars)
     */
    public void saveGame(double moneyInDollars) { // Method to save game state
        // Try-with-resources to ensure PrintWriter is closed automatically
        try (PrintWriter pw = new PrintWriter(new FileWriter(SAVE_FILE))) { // Create PrintWriter for writing
            // Write money with 2 decimal places to file
            pw.printf("%.2f", moneyInDollars); // Format to 2 decimal places and write to file
            System.out.println("Your money had saved!"); // Success message
        } catch (IOException e) { // Catch file writing exceptions
            // Handle file writing errors
            System.out.println("Failed to save game!"); // Error message
        }
    }

    /**
     * Loads the player's saved money from file.
     *
     * @return saved money amount in dollars, or null if no save exists or file is corrupted
     */
    public Double loadGame() { // Method to load saved game state, returns Double wrapper class (nullable)
        // Create File object to check if save file exists
        File file = new File(SAVE_FILE); // Create File object for the save file

        // Return null if save file doesn't exist
        if (!file.exists()) return null; // Check file existence and return null if not found

        // Try-with-resources to ensure BufferedReader is closed automatically
        try (BufferedReader br = new BufferedReader(new FileReader(file))) { // Create BufferedReader for reading
            // Read the first line from the file
            String line = br.readLine(); // Read first (and only) line of the file

            // Check if line is not null and not empty
            if (line != null && !line.trim().isEmpty()) { // Validate line content
                // Parse the string to double and return
                return Double.parseDouble(line.trim()); // Convert string to double and return
            }
        } catch (Exception e) { // Catch any exceptions during reading/parsing
            // Handle any errors during file reading/parsing
            System.err.println("Save file corrupted. Starting fresh!"); // Error message for corrupted file
        }

        // Return null if file is empty or corrupted
        return null; // Return null for any other failure cases
    }

    /**
     * Clears/Deletes the save file.
     *
     * @return true if file was deleted successfully
     */
    public boolean clearSave() { // Method to delete the save file
        File file = new File(SAVE_FILE); // Create File object for the save file
        if (file.exists()) { // Check if the file exists
            boolean deleted = file.delete(); // Attempt to delete the file
            if (deleted) { // Check if deletion was successful
                System.out.println("Save file deleted successfully."); // Success message
            }
            return deleted; // Return deletion status (true/false)
        }
        return false; // Return false if file didn't exist
    }
}