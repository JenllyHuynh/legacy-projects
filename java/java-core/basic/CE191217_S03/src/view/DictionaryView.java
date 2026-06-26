package view;

import service.DictionaryService;
import util.DictionaryException;
import util.InputUtils;

import java.io.IOException;

/**
 * S03 English – English dictionary
 * User interface for dictionary application
 * Handles menu display and user interaction
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class DictionaryView {
    // Service layer instance for business operations
    private final DictionaryService service = new DictionaryService();

    /**
     * Main application loop
     */
    public void start() {
        while (true) {
            // Display main menu
            System.out.println("\n=== ENGLISH DICTIONARY ===");
            System.out.println("1. Create a new word");
            System.out.println("2. Edit a word");
            System.out.println("3. Look up meaning");
            System.out.println("4. Exit");

            // Get user's menu choice
            int choice = InputUtils.readChoice();

            try {
                // Process user's choice
                switch (choice) {
                    case 1 -> createWord();      // Add new word
                    case 2 -> editWord();        // Edit existing word
                    case 3 -> lookupWord();      // Search for word meaning
                    case 4 -> {                  // Exit application
                        System.out.println("Goodbye!");
                        return;  // Exit the application
                    }
                }
            } catch (DictionaryException | IOException e) {
                // Handle errors gracefully
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Handles word creation process
     * @throws DictionaryException If word creation fails
     * @throws IOException If file operations fail
     */
    private void createWord() throws DictionaryException, IOException {
        // Step 1: Get word from user
        String word = InputUtils.readWord();

        // Step 2: Check if word already exists BEFORE asking for meaning
        String existingMeaning = service.lookupWordForCheck(word);
        if (existingMeaning != null) {
            // Word already exists - show error and stop
            System.out.println("\n ERROR: Word \"" + word + "\" already exists!");
            System.out.println("   Current meaning: " + existingMeaning);
            System.out.println("   Please use 'Edit word' option to modify it.");
            return; // Stop here - don't ask for meaning
        }

        // Step 3: Only ask for meaning if word doesn't exist
        String meaning = InputUtils.readMeaning();

        // Step 4: Create the word
        service.createWord(word, meaning);
    }

    /**
     * Handles word editing process
     * @throws DictionaryException If word not found or update fails
     * @throws IOException If file operations fail
     */
    private void editWord() throws DictionaryException, IOException {
        //Get word to edit
        String word = InputUtils.readWord();
        //Check if word exists BEFORE asking for new meaning
        String existingMeaning = service.lookupWordForCheck(word);
        if (existingMeaning == null) {
            // Word doesn't exist - show error and stop
            System.out.println("\nERROR: Word \"" + word + "\" not found!");
            System.out.println("Please use 'Create a new word' option to add it.");
            return; // Stop here - don't ask for meaning
        }
        //Show current meaning
        System.out.println("Current meaning: " + existingMeaning);

        //Only ask for new meaning if word exists
        String meaning = InputUtils.readMeaning();

        //Update the word
        service.updateWord(word, meaning);
    }

    /**
     * Handles word lookup process
     * @throws DictionaryException If word not found
     * @throws IOException If file operations fail
     */
    private void lookupWord() throws DictionaryException, IOException {
        // Get word to look up
        String word = InputUtils.readWord();
        // Look up the word
        service.lookupWord(word);
    }
}