package view;

import model.Player;
import service.SlotMachineService;
import util.InputUtils;

import java.util.Scanner;

/**
 * S04 - Simple Slot Machine
 * View class for the slot machine game interface.
 * Handles user interaction and menu display.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class SlotMachineView { // Main view class for the slot machine game
    // Reference to the service layer containing game logic
    private final SlotMachineService service; // Final field to store service instance
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Constructs a SlotMachineView with the specified service.
     *
     * @param service the service layer to use for game operations
     */
    public SlotMachineView(SlotMachineService service) { // Constructor
        this.service = service; // Initialize the service field with provided parameter
    }

    /**
     * Displays the main menu options to the user.
     */
    private void printMenu() { // Private helper method to display menu
        System.out.println("1) Play the slot machine."); // Option 1: Play game
        System.out.println("2) Save game."); // Option 2: Save current game state
        System.out.println("3) Cash out."); // Option 3: Exit with winnings
    }

    /**
     * Starts the main game loop and handles user interactions.
     */
    public void start() { // Main method that runs the game interface
        // Main game loop
        while (true) { // Infinite loop until player exits or runs out of money
            // Get current player from service
            Player player = service.getPlayer(); // Retrieve current player state

            // Display player's current balance
            System.out.printf("\nYou have %s.\n", player); // Show player's current money

            // Check if player has enough money to continue playing
            if (!player.canPlay()) { // Call Player's canPlay() method to check balance
                System.out.println("You have no money left. Thank you for playing!"); // Game over message
                break;  // Exit loop if player has no money
            }

            // Display menu options
            printMenu(); // Call private method to show menu

            // Get user's menu choice using input utility
            int choice = InputUtils.readChoice(); // Use InputUtils to get validated choice

            // Process user's choice
            switch (choice) { // Switch statement based on user choice
                case 1 -> service.play();        // Play one round
                case 2 -> service.saveGame();    // Save current game state
                case 3 -> handleCashOut(); // Just exit - keep money in save file
            }
        }
    }

    /**
     * Handles cash out with two options.
     * 1) Just exit (keep money for next time)
     * 2) Withdraw all & clear save
     */
    private void handleCashOut() { // Private method for cash out menu
//        Player player = service.getPlayer();
//        System.out.printf("Thank you for playing! You end with %s!\n", player);
//        service.cashOutAndClear();
//        System.exit(0);

        System.out.println("\n=== Cash Out Options ==="); // Display header
        System.out.println("1) Just exit (keep money for next time)"); // Option 1
        System.out.println("2) Withdraw all & clear save"); // Option 2
        System.out.print("Choose an option: "); // Prompt for user input

        try { // Try block to handle potential exceptions
            int cashOutChoice = Integer.parseInt(scanner.nextLine().trim()); // Read and parse user input
            if (cashOutChoice == 1) { // Check if user chose option 1
                // Just exit - keep save
                //  game before exiting
                service.saveGame(); // Call service to save game state
                System.out.printf("Thank you for playing! You have %s saved for next time.\n",
                        service.getPlayer()); // Exit message with current balance
                System.exit(0); // Exit the program
            } else if (cashOutChoice == 2) { // Check if user chose option 2
                // Withdraw all & clear save
                System.out.printf("Cashing out! You withdraw %s\n", service.getPlayer()); // Cash out message
                service.cashOutAndClear(); // Call service to save and clear
                System.exit(0); // Exit the program
            } else { // Handle invalid choice (not 1 or 2)
                System.out.println("Invalid choice. Returning to main menu."); // Error message
            }
        } catch (NumberFormatException e) { // Catch non-numeric input
            System.out.println("Invalid input. Please enter a number."); // Error message for non-numeric
        } catch (Exception e) { // Catch any other exceptions
            System.out.println("Invalid input. Returning to main menu."); // Generic error message
        }
    }
}