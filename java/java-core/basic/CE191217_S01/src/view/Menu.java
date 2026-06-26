package view;

import model.Student;
import service.StudentService;
import util.DataValidationException;
import util.InputUtils;
import java.util.List;

/**
 * S01 Manage student
 * User interface class that handles menu display and user interactions.
 * Acts as the view layer in the MVC architecture.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Menu {
    // Declare a final instance of StudentService (cannot be changed after initialization)
    // This is the service layer instance for handling business logic
    private final StudentService service = new StudentService();

    /**
     * Main application loop that displays menu and processes user choices.
     * Runs continuously until user chooses to exit.
     */
    public void run() {
        // Start infinite loop until user chooses to exit (option 4)
        while (true) {
            // Call method to display main menu options
            showMainMenu();
            // Call utility method to get validated menu choice from user
            int choice = InputUtils.inputMenuChoice();

            // Use try-catch block to handle validation exceptions
            try {
                // Use switch expression (Java 14+ feature) to handle menu choices
                switch (choice) {
                    // Case 1: Enter new student
                    case 1 -> enterStudentList();
                    // Case 2: Search for student
                    case 2 -> lookupStudent();
                    // Case 3: Display all students
                    case 3 -> displayStudentList();
                    // Case 4: Exit program
                    case 4 -> {
                        // Print farewell message
                        System.out.println("Goodbye!");
                        // Exit the method (and the program loop)
                        return;
                    }
                }
            } catch (DataValidationException e) {
                // Catch and display validation errors from service layer
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Displays the main menu options to the user.
     */
    private void showMainMenu() {
        // Print blank line for visual separation
        System.out.println("\n1. Enter student list");
        System.out.println("2. Look up student");
        System.out.println("3. Display student list");
        System.out.println("4. Exit");
    }

    /**
     * Handles the process of entering a new student into the system.
     * Collects all required student information and adds to service layer.
     *
     * @throws DataValidationException if student data fails validation
     */
    private void enterStudentList() throws DataValidationException {
        // Display header for student entry
        System.out.println("Enter new student:");
        // Use utility method to input student code with duplicate checking enabled
        // Pass 'true' to enable duplicate checking and 'service' for validation
        String code = InputUtils.inputStudentCode(true, service);  // Check duplicates
        // Use utility method to input and validate student name
        String name = InputUtils.inputStudentName();
        // Use utility method to input and validate date of birth
        String dob = InputUtils.inputDate();
        // Use utility method to input and validate learning point
        double point = InputUtils.inputPoint();

        // Create a new Student object with the collected data
        Student student = new Student(code, name, dob, point);
        // Call service method to add student (includes final validation and persistence)
        service.addStudent(student);  // Service layer will also do final duplicate check
        // Display success message to user
        System.out.println("Student added successfully!");
    }

    /**
     * Handles student search functionality.
     * Searches for students by name and displays results.
     */
    private void lookupStudent() {
        // Use utility method to get search keyword from user
        String keyword = InputUtils.inputSearchName();
        // Call service method to search for students matching the keyword
        List<Student> found = service.searchByName(keyword);

        // Check if search returned any results
        if (found.isEmpty()) {
            // Display message if no students found
            System.out.println("No student found with name containing: " + keyword);
        } else {
            // Display header for search results
            System.out.println("Found student(s):");
            // Loop through all found students
            for (Student s : found) {
                // Print separator line
                System.out.println("---------------------------");
                // Print student details (calls Student.toString() method)
                System.out.println(s);
            }
            // Print final separator line after all results
            System.out.println("---------------------------");
        }
    }

    /**
     * Displays all students in the system, sorted by name.
     * Shows message if no students exist.
     */
    private void displayStudentList() {
        // Call service method to get all students sorted by name
        List<Student> list = service.getAllStudents();
        // Check if student list is empty
        if (list.isEmpty()) {
            // Display message if no students in system
            System.out.println("Student list is empty!");
            // Exit method early
            return;
        }
        // Display header for student list
        System.out.println("Student list:");
        // Print separator line
        System.out.println("---------------------------");
        // Loop through all students
        for (Student s : list) {
            // Print student details (calls Student.toString() method)
            System.out.println(s);
            // Print separator line between students
            System.out.println("---------------------------");
        }
    }
}