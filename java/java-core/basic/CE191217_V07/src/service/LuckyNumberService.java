package service;

import model.GameStats;

import java.util.Scanner;

/**
 * V07 - The app should provide basic functionality. However, you can add
 *       additional functionality to your app if you wish.
 * Service class for Lucky Number Game logic.
 * Handles game mechanics, user input, and statistics.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class LuckyNumberService {
    // Declare constant for maximum lucky number value
    private static final int MAXIMUM = 100;
    // Declare final GameStats instance to track game statistics
    private final GameStats stats = new GameStats();

    /**
     * Main game play loop.
     * Generates random number and processes user guesses until correct.
     *
     * @param scanner Scanner instance for reading user input
     */
    public void play(Scanner scanner) {
        // Generate random lucky number between 0 and MAXIMUM (inclusive)
        int luckyNumber = (int) (Math.random() * (MAXIMUM + 1));
        // Initialize guess counter
        int guesses = 0;

        int maxPlay = 3;
        System.out.println("so may man: " + luckyNumber);
        // Display game start message
        System.out.println("\n--- New Game Started ---");
        System.out.println("I'm thinking of a number between 0 and " + MAXIMUM + "...");

        // Main game loop
        while (true) {
            // Read valid guess from user
            int guess = readValidGuess(scanner);
            // Increment guess counter
            guesses++;

            if (guesses >= maxPlay && guess != luckyNumber){
                System.out.printf("There are only %d plays", maxPlay);
                System.out.println("\nLucky number: " + luckyNumber);
                stats.addGame(guesses);
                break;
            }

            // Compare guess with lucky number
            if (guess > luckyNumber) {
                // Guess is too high
                System.out.println("Lucky number is smaller than your guess.");
            } else if (guess < luckyNumber) {
                // Guess is too low
                System.out.println("Lucky number is larger than your guess.");
            } else {
                // Correct guess - game won
                System.out.printf("Congratulations! You guessed the lucky number after %d guess%s!%n",
                        guesses, guesses == 1 ? "" : "es");
                // Add game to statistics
                stats.addGame(guesses);
                // Exit game loop
                break;
            }
        }
    }

    /**
     * Reads and validates user guess input.
     * Ensures input is a valid number within game range.
     *
     * @param scanner Scanner instance for reading input
     * @return validated guess number between 0 and MAXIMUM
     */
    private int readValidGuess(Scanner scanner) {
        while (true) {
            // Display prompt with valid range
            System.out.print("Enter your guess (0 - " + MAXIMUM + "): ");
            try {
                // Read and parse user input
                int guess = Integer.parseInt(scanner.nextLine().trim());
                // Validate range
                if (guess >= 0 && guess <= MAXIMUM) {
                    // Return valid guess
                    return guess;
                }
                // Invalid range error
                System.out.println("Please enter a number between 0 and " + MAXIMUM + "!");
            } catch (NumberFormatException e) {
                // Non-numeric input error
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
    }

    /**
     * Asks user if they want to continue playing.
     *
     * @param scanner Scanner instance for reading input
     * @return true if user wants to continue, false otherwise
     */
    public boolean askContinue(Scanner scanner) {
        // Display continue prompt
        System.out.print("Do you want to continue playing? (yes/no): ");
        // Read and normalize response
        String response = scanner.nextLine().trim().toLowerCase();
        // Check for positive responses (y or yes)
        return response.equals("y") || response.equals("yes");
    }

    /**
     * Displays game statistics report.
     * Shows total games, guesses, averages, and best game.
     */
    public void report() {
        // Check if any games have been played
        if (!stats.hasPlayed()) {
            System.out.println("\nNo games played yet. Goodbye!");
            return;
        }

        // Display report header
        System.out.println("\n=== Game Report ===");
        // Display total games played
        System.out.println("Total games played     : " + stats.getTotalGames());
        // Display total guesses made
        System.out.println("Total guesses made     : " + stats.getTotalGuesses());
        // Display average guesses with 2 decimal places
        System.out.printf("Average guesses per game: %.2f%n", stats.getAverageGuesses());
        // Display best game performance
        System.out.println("Best game (fewest guesses): " + stats.getBestGame());
        // Display closing message
        System.out.println("Thank you for playing!");
    }

    /**
     * Provides access to game statistics.
     *
     * @return GameStats object containing current statistics
     */
    public GameStats getStats() {
        // Return game statistics object
        return stats;
    }
}