package model;

/**
 * S03 English – English dictionary
 * Represents a word entry in the dictionary
 * Contains the word itself and its meaning
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Word {
    // The word (stored in lowercase for consistency)
    private final String word;
    // The definition/meaning of the word
    private String meaning;

    /**
     * Constructor to create a new word entry
     * @param word The word (will be converted to lowercase)
     * @param meaning The definition of the word
     */
    public Word(String word, String meaning) {
        // Convert to lowercase and remove extra spaces
        //this.word = word.toLowerCase().trim();
        this.word = word.trim(); // Remove leading and trailing whitespace from the input word
        this.meaning = meaning.trim(); // Remove leading and trailing whitespace from the input meaning
    }

    /**
     * Get the word
     * @return The word in lowercase
     */
    public String getWord() {
        return word;
    }

    /**
     * Get the meaning of the word
     * @return The meaning
     */
    public String getMeaning() {
        return meaning;
    }

    /**
     * Update the meaning of the word
     * @param meaning The new meaning
     */
    public void setMeaning(String meaning) {
        this.meaning = meaning.trim();
    }

    /**
     * String representation of the word
     * @return "word : meaning" format
     */
    @Override
    public String toString() {
        return word + " : " + meaning;
    }
}