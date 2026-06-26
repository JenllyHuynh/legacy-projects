package repository;

import model.Word;
import util.DictionaryException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * S03 English – English dictionary
 * Manages dictionary data storage in files
 * Stores words in separate files by first letter
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class DictionaryRepository { // Main class for dictionary data persistence

    /**
     * Generates index filename based on first letter
     * @param c First character of the word
     * @return Filename like "a_index.dat"
     */
    private String getIndexFile(char c) { // Private helper method
        return c + "_index.dat"; // Concatenates character with fixed filename suffix
    }

    /**
     * Generates meaning filename based on first letter
     * @param c First character of the word
     * @return Filename like "a_meaning.dat"
     */
    private String getMeaningFile(char c) { // Private helper method
        return c + "_meaning.dat"; // Concatenates character with fixed filename suffix
    }

    /**
     * Extracts first character of a word (case-insensitive)
     * @param word The word to process
     * @return Lowercase first character
     */
    private char getFirstChar(String word) { // Private helper method
        if (word == null || word.isEmpty()) { // Checks for null or empty input
            return '_'; // Returns underscore as default value for invalid input
        }
        return Character.toLowerCase(word.charAt(0)); // Extracts first char and converts to lowercase
    }

    /**
     * Creates a file if it doesn't exist
     * @param path File path to check/create
     * @throws IOException If file creation fails
     */
    private void createFileIfNotExists(String path) throws IOException { // Private helper method
        File file = new File(path); // Creates File object from path string
        if (!file.exists()) { // Checks if file already exists
            file.createNewFile(); // Creates new empty file if it doesn't exist
        }
    }

    /**
     * Checks if a word exists in the dictionary
     * @param word Word to check
     * @return true if word exists, false otherwise
     * @throws IOException If file reading fails
     */
    private boolean wordExists(String word) throws IOException { // Private helper method
        if (word == null || word.trim().isEmpty()) { // Checks for null or whitespace-only input
            return false; // Returns false for invalid input
        }

        String normalizedWord = word.toLowerCase().trim(); // Converts to lowercase and trims spaces
        char c = getFirstChar(normalizedWord); // Gets first character for file naming
        String indexPath = getIndexFile(c); // Generates index filename
        File indexFile = new File(indexPath); // Creates File object

        if (!indexFile.exists()) { // Checks if file exists
            return false; // Returns false if file doesn't exist
        }

        try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) { // Try-with-resources
            String line; // Variable to hold each line read
            while ((line = br.readLine()) != null) { // Loops until end of file
                String normalizedLine = line.trim().toLowerCase(); // Normalizes line from file
                if (normalizedLine.equals(normalizedWord)) { // Compares normalized strings
                    return true; // Returns true if word is found
                }
            }
        }
        return false; // Returns false if word not found
    }

    /**
     * Adds a new word to the dictionary
     * @param word Word object to add
     * @throws IOException If file operations fail
     * @throws DictionaryException If word already exists
     */
    public void addWord(Word word) throws IOException, DictionaryException { // Public method for adding words
        if (word == null) { // Checks if Word object is null
            throw new IllegalArgumentException("Word cannot be null"); // Throws runtime exception
        }

        String wordToAdd = word.getWord(); // Extracts word string from Word object
        if (wordToAdd == null || wordToAdd.trim().isEmpty()) { // Validates word string
            throw new DictionaryException("Word cannot be empty!"); // Throws custom exception
        }

        if (wordExists(wordToAdd)) { // Checks if word already exists
            throw new DictionaryException("Word \"" + wordToAdd + "\" already exists!"); // Throws exception for duplicate
        }

        char c = getFirstChar(wordToAdd); // Gets first character for file naming

        createFileIfNotExists(getIndexFile(c)); // Creates index file if needed
        createFileIfNotExists(getMeaningFile(c)); // Creates meaning file if needed

        try (BufferedWriter indexWriter = new BufferedWriter(new FileWriter(getIndexFile(c), true));
             BufferedWriter meaningWriter = new BufferedWriter(new FileWriter(getMeaningFile(c), true))) { // Try-with-resources

            indexWriter.write(wordToAdd); // Writes word string
            indexWriter.newLine(); // Adds newline character

            meaningWriter.write(word.getMeaning() != null ? word.getMeaning() : ""); // Writes meaning or empty string
            meaningWriter.newLine(); // Adds newline character
        }
    }

    /**
     * Updates the meaning of an existing word
     * IMPORTANT: This also updates the word's case (capitalization)
     * @param word Word to update (with desired capitalization)
     * @param newMeaning New meaning for the word
     * @throws IOException If file operations fail
     * @throws DictionaryException If word not found
     */
    public void updateWord(String word, String newMeaning) throws IOException, DictionaryException { // Public method
        if (word == null || word.trim().isEmpty()) { // Validates input word
            throw new DictionaryException("Word cannot be empty!"); // Throws exception for invalid input
        }

        // Normalize for searching (lowercase for comparison)
        String normalizedWord = word.toLowerCase().trim(); // Normalizes search word to lowercase
        // Keep original input for replacement
        String originalInputWord = word.trim(); // Preserves original capitalization from user input

        char c = getFirstChar(normalizedWord); // Gets first character
        String indexPath = getIndexFile(c); // Gets index file path
        String meaningPath = getMeaningFile(c); // Gets meaning file path

        if (!Files.exists(Paths.get(indexPath))) { // Checks file existence
            throw new DictionaryException("Word \"" + word + "\" not found!"); // Throws exception if file doesn't exist
        }

        List<String> words = new ArrayList<>(); // Creates list for words
        List<String> meanings = new ArrayList<>(); // Creates list for meanings
        boolean found = false; // Flag to track if word was found

        // Read all existing entries - READ BOTH FILES TOGETHER
        try (BufferedReader indexReader = new BufferedReader(new FileReader(indexPath));
             BufferedReader meaningReader = new BufferedReader(new FileReader(meaningPath))) { // Try-with-resources

            String currentWord; // Variable for reading word lines
            int lineNumber = 0; // Track line number for error reporting

            // Read index file line by line
            while ((currentWord = indexReader.readLine()) != null) { // Reads until end of index file
                lineNumber++; // Increment line counter
                String trimmedWord = currentWord.trim(); // Remove surrounding spaces

                // Read corresponding meaning line (handle missing meanings)
                String currentMeaning = meaningReader.readLine(); // Read from meaning file
                if (currentMeaning == null) { // Check if meaning file has fewer lines
                    // If meaning file is shorter, use empty string
                    System.err.println("Warning: Missing meaning for word '" + trimmedWord + "' at line " + lineNumber); // Error message
                    currentMeaning = ""; // Default to empty string
                }

                // Check if this is the word we want to update (case-insensitive comparison)
                if (trimmedWord.toLowerCase().equals(normalizedWord)) { // Compare normalized versions
                    // Use the original input word (with user's desired case)
                    words.add(originalInputWord); // Add user's capitalized version
                    meanings.add(newMeaning != null ? newMeaning.trim() : ""); // Add new meaning
                    found = true; // Sets found flag to true
                } else {
                    // Keep existing word and meaning
                    words.add(trimmedWord); // Add existing word as-is
                    meanings.add(currentMeaning != null ? currentMeaning.trim() : ""); // Add existing meaning
                }
            }

            // Check if meaning file has extra lines (data corruption)
            String extraMeaning; // Variable for extra lines
            while ((extraMeaning = meaningReader.readLine()) != null) { // Check for leftover lines
                System.err.println("Warning: Extra meaning line found (no corresponding word): " + extraMeaning); // Warning message
            }
        }

        if (!found) { // Checks found flag
            throw new DictionaryException("Word \"" + word + "\" not found!"); // Throws exception
        }

        // Verify data integrity before writing
        if (words.size() != meanings.size()) { // Check if lists have same size
            throw new IOException("Data corruption detected: index and meaning files have different number of entries"); // Throw exception
        }

        // Write updated data back to files
        try (BufferedWriter indexWriter = new BufferedWriter(new FileWriter(indexPath));
             BufferedWriter meaningWriter = new BufferedWriter(new FileWriter(meaningPath))) { // Try-with-resources

            for (int i = 0; i < words.size(); i++) { // Loops through all entries
                indexWriter.write(words.get(i)); // Writes word to index file
                indexWriter.newLine(); // Adds newline
                meaningWriter.write(meanings.get(i)); // Writes meaning to meaning file
                meaningWriter.newLine(); // Adds newline
            }
        }
    }

    /**
     * Looks up the meaning of a word
     * @param word Word to look up
     * @return Meaning of the word
     * @throws IOException If file operations fail
     * @throws DictionaryException If word not found
     */
    public String lookupWord(String word) throws IOException, DictionaryException { // Public method
        if (word == null || word.trim().isEmpty()) { // Validates input
            throw new DictionaryException("Word cannot be empty!"); // Throws exception
        }

        String normalizedWord = word.toLowerCase().trim(); // Normalizes search word
        char c = getFirstChar(normalizedWord); // Gets first character
        String indexPath = getIndexFile(c); // Gets index file path

        if (!Files.exists(Paths.get(indexPath))) { // Checks file existence
            throw new DictionaryException("Word \"" + word + "\" not found!"); // Throws exception
        }

        try (BufferedReader indexReader = new BufferedReader(new FileReader(indexPath));
             BufferedReader meaningReader = new BufferedReader(new FileReader(getMeaningFile(c)))) { // Try-with-resources

            String currentWord; // Variable for reading word lines
            while ((currentWord = indexReader.readLine()) != null) { // Reads until end of file
                String currentMeaning = meaningReader.readLine(); // Read corresponding meaning

                // Handle missing meaning
                if (currentMeaning == null) { // Check if meaning file has fewer lines
                    currentMeaning = ""; // Default to empty string
                }

                if (currentWord.trim().toLowerCase().equals(normalizedWord)) { // Compare normalized words
                    return currentMeaning.trim(); // Returns meaning or empty string
                }
            }
        }

        throw new DictionaryException("Word \"" + word + "\" not found!"); // Throws exception if loop completes without finding word
    }
}