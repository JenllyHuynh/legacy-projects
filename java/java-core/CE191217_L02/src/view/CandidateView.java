package view;

import service.CandidateService;
import util.InputUtils;

/**
 * L02 - Create a Java console program to manage Candidates of company
 * View class for Candidate Management System interface.
 * Provides menu-driven interface for managing different types of candidates.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class CandidateView {
    // Declare a private final CandidateService instance for candidate management operations
    private final CandidateService service = new CandidateService();

    /**
     * Starts the Candidate Management System main loop.
     * Displays welcome message and handles menu navigation until exit.
     */
    public void start() {
        // Display welcome banner
        System.out.println("\n======================================");
        System.out.println("|   CANDIDATE MANAGEMENT SYSTEM      |");
        System.out.println("======================================");

        // Main program loop
        while (true) {
            // Display main menu options
            displayMenu();
            // Read user choice from input
            int choice = InputUtils.readMenuChoice();

            // Handle user choice using switch statement
            switch (choice) {
                case 1 -> createExperienceLoop();  // Create Experience candidates
                case 2 -> createFresherLoop();     // Create Fresher candidates
                case 3 -> createInternLoop();      // Create Intern candidates
                case 4 -> service.searchCandidates();  // Search candidates
                case 5 -> {
                    // Exit the program
                    System.out.println("\nThank you! Goodbye!");
                    return;
                }
            }
        }
    }

    /**
     * Handles loop for creating multiple Experience candidates.
     * Continues creating until user chooses to stop.
     */
    private void createExperienceLoop() {
        do {
            // Create a single Experience candidate
            service.createExperience();
            // Ask if user wants to continue
            System.out.print("\nContinue creating Experience candidates? ");
        } while (InputUtils.readContinueYN());  // Continue if user answers Yes
    }

    /**
     * Handles loop for creating multiple Fresher candidates.
     * Continues creating until user chooses to stop.
     */
    private void createFresherLoop() {
        do {
            // Create a single Fresher candidate
            service.createFresher();
            // Ask if user wants to continue
            System.out.print("\nContinue creating Fresher candidates? ");
        } while (InputUtils.readContinueYN());  // Continue if user answers Yes
    }

    /**
     * Handles loop for creating multiple Intern candidates.
     * Continues creating until user chooses to stop.
     */
    private void createInternLoop() {
        do {
            // Create a single Intern candidate
            service.createIntern();
            // Ask if user wants to continue
            System.out.print("\nContinue creating Intern candidates? ");
        } while (InputUtils.readContinueYN());  // Continue if user answers Yes
    }

    /**
     * Displays the main menu options for Candidate Management System.
     * Uses ASCII art style formatting for visual appeal.
     */
    private void displayMenu() {
        // Display menu with border formatting
        System.out.println("\n============== MAIN MENU =============");
        System.out.println("| 1. Create Experience Candidate     |");
        System.out.println("| 2. Create Fresher Candidate        |");
        System.out.println("| 3. Create Intern Candidate         |");
        System.out.println("| 4. Search Candidates               |");
        System.out.println("| 5. Exit                            |");
        System.out.println("======================================");
    }
}