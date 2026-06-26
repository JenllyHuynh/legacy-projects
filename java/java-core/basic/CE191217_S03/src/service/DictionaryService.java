package service;

import model.Word;
import repository.DictionaryRepository;
import util.DictionaryException;

import java.io.IOException;

/**
 * S03 English – English dictionary
 * Contains business logic for dictionary operations
 * Mediates between View and Repository layers
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class DictionaryService {
    // Repository instance for data access
    private final DictionaryRepository repository = new DictionaryRepository();

    /**
     * Creates a new word in the dictionary
     * @param word Word to add
     * @param meaning Meaning of the word
     * @throws DictionaryException If word already exists or validation fails
     * @throws IOException If file operations fail
     */
    public void createWord(String word, String meaning) throws DictionaryException, IOException {
        // Create Word object
        Word w = new Word(word, meaning);
        // Add to repository
        repository.addWord(w);
        // Display success message
        System.out.println("Word \"" + word + "\" added successfully!");
    }

    /**
     * Updates the meaning of an existing word
     * @param word Word to update
     * @param newMeaning New meaning
     * @throws DictionaryException If word not found
     * @throws IOException If file operations fail
     */
    public void updateWord(String word, String newMeaning) throws DictionaryException, IOException {
        // Update in repository
        repository.updateWord(word, newMeaning);
        // Display success message
        System.out.println("Word \"" + word + "\" updated successfully!");
    }

    /**
     * Looks up and displays the meaning of a word
     * @param word Word to look up
     * @throws DictionaryException If word not found
     * @throws IOException If file operations fail
     */
    public void lookupWord(String word) throws DictionaryException, IOException {
        // Retrieve meaning from repository
        String meaning = repository.lookupWord(word);
        // Display meaning
        System.out.println("Meaning: " + meaning);
    }

    /**
     * Checks if a word exists (for validation purposes)
     * @param word Word to check
     * @return Meaning if word exists, null otherwise
     * @throws IOException If file operations fail
     */
    public String lookupWordForCheck(String word) throws IOException {
        try {
            // Try to look up the word
            return repository.lookupWord(word);
        } catch (DictionaryException e) {
            // Word not found - return null
            return null;
        }
    }
}