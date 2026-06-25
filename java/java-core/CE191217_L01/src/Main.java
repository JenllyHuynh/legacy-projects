import view.StudentView;

/**
 * L01 -Create a Java console program to manage students
 * Main application entry point for Student Management System.
 * Launches the student management interface.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Main {
    /**
     * Main method to launch the Student Management System.
     * Creates and starts the StudentView.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create a new StudentView instance and start the application
        new StudentView().start();
    }
}