package service;

import model.Player;
import repository.GameRepository;

import java.util.Random;

/**
 * S04 - Simple Slot Machine
 * Service class containing the business logic for the slot machine game.
 * Handles game mechanics, win calculations, and game state management.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class SlotMachineService { // Main service class containing game logic
    // Reference to the player object
    private final Player player; // Final field to store player instance
    // Random number generator for slot machine results
    private final Random random = new Random(); // Random instance for generating slot numbers
    // Repository for saving and loading game data
    private final GameRepository repository = new GameRepository(); // Repository instance for data operations

    //    private static final boolean TEST_MODE = true;
    //    private static final int TEST_CASE = 4;


    /**
     * Constructs a SlotMachineService with the specified player.
     *
     * @param player the player who will use this service
     */
    public SlotMachineService(Player player) { // Constructor
        this.player = player; // Initialize the player field with provided parameter
    }

    /**
     * Calculates winnings based on slot machine results.
     *
     * @param a first slot value (0-9)
     * @param b second slot value (0-9)
     * @param c third slot value (0-9)
     * @return winnings in cents: 1000 for three of a kind, 50 for a pair, 0 otherwise
     */
    private int calculateWinnings(int a, int b, int c) { // Private helper method for win calculation
        // Check for three of a kind (jackpot)
        if (a == b && b == c) { // All three numbers are equal
            return 1000;  // $10.00 in cents (1000 cents = $10.00)
        }
        // Check for any pair
        if (a == b && a % 2 == 0 || b == c && b % 2 == 0) { // Any two numbers are equal
            return 50;    // $0.50 in cents (50 cents = $0.50)
        }
        // No winning combination
        return 0; // Return 0 for no win
    }

    /**
     * Executes one play of the slot machine.
     * Deducts bet, generates results, calculates winnings, and updates player's money.
     */
    public void play() { // Main game play method
        // Check if player has enough money to play
        if (!player.canPlay()) { // Call Player's canPlay() method
            System.out.println("You don't have enough money to play!"); // Error message
            return;  // Exit if insufficient funds
        }

        // Deduct the bet amount (25 cents)
        player.deductBet(); // Deduct $0.25 from player's money

//        int d1,d2,d3;
//        if (TEST_MODE && TEST_CASE != 0) {
//            switch (TEST_CASE) {
//                case 1 -> { d1 = 2; d2 = 2; d3 = 0; }  // 220
//                case 2 -> { d1 = 0; d2 = 2; d3 = 2; }  // 022
//                case 3 -> { d1 = 1; d2 = 1; d3 = 0; }  // 110
//                case 4 -> { d1 = 4; d2 = 4; d3 = 4; }  // 444 - THẮNG LỚN
//                case 5 -> { d1 = 2; d2 = 0; d3 = 2; }  // 202
//                default -> {
//                    d1 = random.nextInt(10);
//                    d2 = random.nextInt(10);
//                    d3 = random.nextInt(10);
//                }
//            }
//            System.out.println("[TEST MODE] Using test case " + TEST_CASE);
//        } else {
//            d1 = random.nextInt(10);
//            d2 = random.nextInt(10);
//            d3 = random.nextInt(10);
//        }

        // Generate three random numbers (0-9) for the slot machine
        int d1 = random.nextInt(10); // Random number 0-9
        int d2 = random.nextInt(10); // Random number 0-9
        int d3 = random.nextInt(10); // Random number 0-9

        // Format and display the result
        String result = String.format("%d%d%d", d1, d2, d3); // Format as three-digit string
        System.out.println("The slot machine shows " + result + "."); // Display slot result

        // Calculate winnings based on the generated numbers
        int winCents = calculateWinnings(d1, d2, d3); // Calculate potential winnings

        // Process winnings if any
        if (winCents > 0) { // Check if player won anything
            // Add winnings to player's money
            player.addWinnings(winCents); // Add winnings to player's balance

            // Display appropriate win message
            if (winCents == 1000) {
                System.out.printf("You win the big prize, $%.2f!\n", winCents / 100.0);
            } else {
                // Regular win - check what type of pair
                String winType = getWinType(d1, d2, d3);
                System.out.printf("You win $%.2f! %s\n", winCents / 100.0, winType);
            }
        } else {
            // Display loss message with explanation
            String lossReason = getLossReason(d1, d2, d3);
            System.out.println(lossReason);
        }
    }

    /**
     * Determines the win type based on three numbers.
     * Checks for matching patterns and returns descriptive win type.
     *
     * @param a the first number to compare
     * @param b the second number to compare
     * @param c the third number to compare
     * @return descriptive win type string if numbers match in winning pattern,
     * empty string if no winning pattern
     */
    private String getWinType(int a, int b, int c) {
        // Check if all three numbers are equal (Three of a kind)
        if (a == b && b == c) {
            // Return win type for three matching numbers
            return "(Three of a kind)";
        }
        // Check if first two numbers are equal
        if (a == b) {
            // Return win type for first two numbers matching
            return "(First two numbers match)";
        }
        // Check if last two numbers are equal
        if (b == c) {
            // Return win type for last two numbers matching
            return "(Last two numbers match)";
        }
        // Check if first and third numbers are equal
        if (a == c) {
            // Return win type for first and third numbers matching
            return "(First and third numbers match)";
        }
        // Return empty string if no winning pattern found
        return "";
    }

    /**
     * Determines the loss reason when no winning combination is achieved.
     * Identifies which numbers match (if any) but not in winning pattern.
     *
     * @param a the first number to compare
     * @param b the second number to compare
     * @param c the third number to compare
     * @return detailed loss reason with matching information,
     * basic loss message if no matches found
     */
    private String getLossReason(int a, int b, int c) {
        // Check if no numbers match at all
        if (a != b && a != c && b != c) {
            // Return basic loss message for no matches
            return "Sorry you don't win anything.";
        }

        // Check if first two numbers match (but not winning combination)
        if (a == b) {
            return "Sorry you don't win anything. (First two match but not a winning combination)";
        }
        // Check if last two numbers match (but not winning combination)
        if (b == c) {
            return "Sorry you don't win anything. (Last two match but not a winning combination)";
        }
        // Check if first and third numbers match (but not winning combination)
        if (a == c) {
            return "Sorry you don't win anything. (First and third match but not a winning combination)";
        }

        // Return complete loss reason string
        return "Sorry you don't win anything.";
    }

    /**
     * Saves the current game state to persistent storage.
     */
    public void saveGame() { // Method to save game progress
        repository.saveGame(player.getMoneyInDollars()); // Call repository to save player's money
    }

    /**
     * Cashes out all money and clears the saved game.
     */
    public void cashOutAndClear() { // Method to cash out and remove save file
        repository.saveGame(player.getMoneyInDollars()); // Save current money amount
        repository.clearSave(); // Delete the save file
    }

    /**
     * Gets the player associated with this service.
     *
     * @return the player object
     */
    public Player getPlayer() { // Getter method for player
        return player; // Return player instance
    }
}