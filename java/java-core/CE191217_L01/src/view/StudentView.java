package view;

import service.StudentService;
import util.InputUtils;

/**
 * L01 -Create a Java console program to manage students
 * View class for Student Management System interface.
 * Provides menu-driven interface for student management operations.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class StudentView {
    // Declare a private final StudentService instance for business operations
    private final StudentService service = new StudentService();

    /**
     * Starts the Student Management System main loop.
     * Displays welcome message and handles menu navigation until exit.
     */
    public void start() {
        // Display welcome banner
        System.out.println("\n==================================");
        System.out.println("|  WELCOME TO STUDENT MANAGEMENT |");
        System.out.println("==================================");

        // Main program loop
        while (true) {
            // Display menu options
            displayMenu();
            // Read user choice from input
            int choice = InputUtils.readMenuChoice();

            // Handle user choice using switch statement
            switch (choice) {
                case 1 -> service.createStudents();  // Create students
                case 2 -> service.findAndSort();     // Find and sort students
                case 3 -> service.updateOrDelete();  // Update or delete student
                case 4 -> service.report();          // Generate report
                case 5 -> {
                    // Exit the program
                    System.out.println("\nThank you for using Student Management System!");
                    System.out.println("Goodbye!");
                    return;
                }
            }
        }
    }

    /**
     * Displays the main menu options for Student Management System.
     * Uses ASCII art style formatting for visual appeal.
     */
    private void displayMenu() {
        // Display menu with border formatting
        System.out.println("|                                |");
        System.out.println("============== MENU ==============");
        System.out.println("| 1. Create Students             |");
        System.out.println("| 2. Find and Sort               |");
        System.out.println("| 3. Update/Delete               |");
        System.out.println("| 4. Report                      |");
        System.out.println("| 5. Exit                        |");
        System.out.println("==================================");
    }
}