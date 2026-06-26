package repository;

import model.Student;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * S01 Manage student
 * Repository class for handling student data persistence.
 * Manages file operations for saving and loading student records.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class StudentRepository {
    // Declare a constant string for the data file name (final means cannot be changed)
    private static final String FILE_NAME = "Student.txt";

    /**
     * Saves all students to file with automatic sorting and error handling.
     * Creates file if it doesn't exist and provides detailed error messages.
     *
     * @param students the list of students to save (ignored if null)
     */
    public static void saveAll(List<Student> students) {
        // Check if the students list is null to prevent NullPointerException
        if (students == null) return;  // Exit method early if list is null

        // Sort the students list using natural ordering (by name, as defined in Student class)
        Collections.sort(students);

        // Use try-with-resources to automatically close the BufferedWriter
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
            // Loop through each student in the list
            for (Student s : students) {
                // Write the student data in file format to the file
                bw.write(s.toFileString());  // Write student data in file format
                // Write a newline character to separate records
                bw.newLine();                 // Add newline after each record
            }
            // If we reach here, save operation was successful
            // No success message to avoid cluttering console
        } catch (IOException e) {
            // Handle file writing errors with detailed information
            // Print error message to standard error stream
            System.err.println("Cannot save to file Student.txt!");
            // Print the specific reason for the error
            System.err.println("Reason: " + e.getMessage());
            // Provide user guidance on what might be wrong
            System.err.println("Your data has been lost this time. Please check folder permission!");
        } catch (Exception e) {
            // Catch any other unexpected errors during save operation
            // Generic error handler for unexpected exceptions
            System.err.println("Unexpected error while saving data!");
        }
    }

    /**
     * Loads all students from file with 100% safety and error recovery.
     * Returns empty list if file doesn't exist or has errors.
     *
     * @return list of successfully loaded students (never null)
     */
    public static List<Student> loadAll() {
        // Initialize an empty ArrayList to store loaded students
        List<Student> list = new ArrayList<>();

        // Check if the data file exists before attempting to read
        // Using Paths.get() to create a Path object from the file name
        if (!Files.exists(Paths.get(FILE_NAME))) {
            // File doesn't exist - normal case for first run of application
            return list;  // Return empty list immediately
        }

        // File exists - proceed with reading using try-with-resources
        //  ensures BufferedReader is closed automatically
        try (BufferedReader br = Files.newBufferedReader(Paths.get(FILE_NAME))) {
            // Declare a variable to hold each line read from the file
            String line;
            // Read the file line by line until null (end of file)
            // The assignment in while condition reads a line and checks if it's not null
            while ((line = br.readLine()) != null) {
                // Skip empty lines or lines containing only whitespace characters
                if (line.trim().isEmpty()) continue;  // Continue to next iteration

                // Split the line into maximum 4 parts using '#' as delimiter
                // The "4" parameter ensures we get at most 4 parts (prevents extra splits)
                String[] parts = line.split("#", 4);
                // Check if line has exactly 4 parts (code, name, dob, learningPoint)
                if (parts.length != 4) continue; // Skip malformed lines (wrong number of parts)

                // Try to parse and create student from valid line
                try {
                    // Extract and clean data from each part
                    // Trim removes leading/trailing whitespace from each field
                    String code = parts[0].trim();        // Student code (first part)
                    String name = parts[1].trim();        // Student name (second part)
                    String dob = parts[2].trim();         // Date of birth (third part)
                    // Parse the fourth part as double (learning point)
                    double point = Double.parseDouble(parts[3].trim());  // Learning point

                    // Create new Student object using the extracted data
                    // Add the newly created student to the list
                    list.add(new Student(code, name, dob, point));
                } catch (NumberFormatException e) {
                    // Handle case where learning point is not a valid number
                    // Skip this line and continue with next one silently
                    continue;  // Skip this line and continue with next one
                }
            }
        } catch (IOException e) {
            // Handle file reading errors gracefully
            // Print error message to standard error stream
            System.err.println("Cannot read file Student.txt!");
            // Inform user that application will continue with empty list
            System.err.println("Starting with empty student list.");
            // Application continues running with empty list - no crash
        }

        // Return the loaded student list (maybe empty if file doesn't exist or has errors)
        return list;
    }
}
