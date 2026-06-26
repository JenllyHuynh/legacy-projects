package view;

import service.LuckyNumberService;

import java.util.Scanner;

/**
 * V07 - The app should provide basic functionality. However, you can add
 *       additional functionality to your app if you wish.
 * View class for Lucky Number Game interface.
 * Handles user interaction and game flow.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class LuckyNumberView {
    // Declare final LuckyNumberService instance for game logic
    private final LuckyNumberService service = new LuckyNumberService();
    // Declare final Scanner instance for reading user input
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Starts the Lucky Number Game.
     * Displays welcome message and manages game sessions.
     */
    public void start() {
        // Display welcome message and game instructions
        System.out.println("=== Welcome to Lucky Number Game! ===");
        System.out.println("I will think of a number between 0 and 100.");
        System.out.println("Try to guess it with as few attempts as possible!\n");

        // Main game loop - play at least once, continue based on user choice
        do {
            // Play a single game session
            service.play(scanner);
        } while (service.askContinue(scanner)); // Continue if user wants to play again

        // Display final game statistics
        service.report();

    }
}