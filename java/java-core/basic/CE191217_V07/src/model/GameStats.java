package model;

/**
 * V07 - The app should provide basic functionality. However, you can add
 *       additional functionality to your app if you wish.
 * Game statistics tracking class.
 * Records and calculates game performance metrics.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class GameStats {
    // Declare private int variable to store total number of games played
    private int totalGames = 0;
    // Declare private int variable to store total number of guesses made across all games
    private int totalGuesses = 0;
    // Declare private int variable to store best (fewest) guesses in a single game
    private int bestGame = Integer.MAX_VALUE;    // Initialized to maximum integer value to track minimum

    /**
     * Adds a completed game to statistics.
     * Updates total games, total guesses, and best game record.
     *
     * @param guesses number of guesses made in the completed game
     */
    public void addGame(int guesses) {
        // Increment total games counter
        totalGames++;
        // Add current game's guesses to total guesses
        totalGuesses += guesses;
        // Update best game if current game has fewer guesses
        if (guesses < bestGame) {
            bestGame = guesses;
        }
    }

    /**
     * Retrieves the total number of games played.
     *
     * @return the total number of games that have been played
     */
    public int getTotalGames() {
        // Return total games value
        return totalGames;
    }

    /**
     * Retrieves the total number of guesses made across all games.
     *
     * @return the cumulative number of guesses made in all games
     */
    public int getTotalGuesses() {
        // Return total guesses value
        return totalGuesses;
    }

    /**
     * Calculates average guesses per game.
     *
     * @return average guesses per game, 0 if no games played
     */
    public double getAverageGuesses() {
        // Calculate average: total guesses divided by total games
        // Return 0 if no games played to avoid division by zero
        return totalGames == 0 ? 0 : (double) totalGuesses / totalGames;
    }

    /**
     * Gets the best game performance (fewest guesses).
     *
     * @return fewest guesses in any game, 0 if no games played
     */
    public int getBestGame() {
        // Return best game value, 0 if no games played yet
        return bestGame == Integer.MAX_VALUE ? 0 : bestGame;
    }

    /**
     * Checks if any games have been played.
     *
     * @return true if at least one game has been played, false otherwise
     */
    public boolean hasPlayed() {
        // Check if total games is greater than zero
        return totalGames > 0;
    }
}
