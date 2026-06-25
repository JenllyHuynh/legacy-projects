package service;

import model.Student;
import repository.StudentRepository;
import util.DataValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * S01 Manage student
 * Service layer containing business logic for student management.
 * Handles operations like adding, searching, and retrieving students.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class StudentService {
    // Declare a final List of Student objects (final means reference cannot be changed)
    // This is the in-memory data store for students
    private final List<Student> students;

    /**
     * Constructor initializes student list by loading from repository.
     */
    public StudentService() {
        // Initialize the students list by loading data from file via repository
        students = StudentRepository.loadAll();  // Load students from file on startup
    }

    /**
     * Adds a new student to the system with validation.
     *
     * @param student the student to add
     * @throws DataValidationException if student data is invalid
     */
    public void addStudent(Student student) throws DataValidationException {
        // Validate required fields are not null or empty
        // Check if student code is null or empty (after trimming)
        if (student.getCode() == null || student.getCode().trim().isEmpty()) {
            // Throw exception with error message if validation fails
            throw new DataValidationException("Student code cannot be empty!");
        }
        // Check if student name is null or empty (after trimming)
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            // Throw exception with error message if validation fails
            throw new DataValidationException("Student name cannot be empty!");
        }
        // Check if date of birth is null or empty (after trimming)
        if (student.getDob() == null || student.getDob().trim().isEmpty()) {
            // Throw exception with error message if validation fails
            throw new DataValidationException("Date of birth cannot be empty!");
        }

        // Clean data - remove extra spaces and normalize
        // Clean code: trim, remove all whitespace, convert to uppercase
        String code = student.getCode().trim().replaceAll("\\s+", "").toUpperCase();
        // Clean name: trim, replace multiple spaces with single space
        String name = student.getName().trim().replaceAll("\\s+", " ");
        // Clean date of birth: trim, remove all whitespace
        String dob = student.getDob().trim().replaceAll("\\s+", "");

        // Validate no '#' character in critical fields (used as delimiter in file)
        // Check if cleaned code contains '#' character
        if (code.contains("#") || name.contains("#") || dob.contains("#")) {
            // Throw exception if '#' found (reserved for file format)
            throw new DataValidationException("Data cannot contain '#' character!");
        }

        // Validate student code format (2 letters + 3 digits)
        // Use regex to check format: exactly 2 uppercase letters followed by exactly 3 digits
        if (!code.matches("^[A-Z]{2}\\d{3}$")) {
            // Throw exception with format example if validation fails
            throw new DataValidationException("Student code must be in format: 2 letters followed by 3 digits (e.g., SV001)!");
        }

        // Validate name contains only letters, spaces, and apostrophes
        // Use regex to check name contains only allowed characters
        if (!name.matches("^[A-Za-z\\s']+$")) {
            // Throw exception if name contains invalid characters
            throw new DataValidationException("Student name can only contain letters, spaces and apostrophes!");
        }

        // Check for duplicate student code (case-insensitive)
        // Call private helper method to check for duplicates
        if (isDuplicateCode(code)) {
            // Throw exception with duplicate code if found
            throw new DataValidationException("Student code " + code + " already exists in the system!");
        }

        // Validate learning point range
        // Get learning point from student object
        double point = student.getLearningPoint();
        // Check if point is within valid range (0.0 to 4.0 inclusive)
        if (point < 0 || point > 4.0) {
            // Throw exception if point is outside valid range
            throw new DataValidationException("Learning point must be between 0.0 and 4.0!");
        }

        // Create clean student object with validated and cleaned data
        // Create new Student object with cleaned data
        Student cleanStudent = new Student(code, name, dob, point);

        // Add valid student to list
        // Add the clean student to the in-memory list
        students.add(cleanStudent);
        // Persist changes to file by saving all students
        StudentRepository.saveAll(students);  // Persist changes to file
    }

    /**
     * Checks if a student code already exists in the system.
     * Comparison is case-insensitive.
     *
     * @param code the student code to check
     * @return true if code already exists, false otherwise
     */
    private boolean isDuplicateCode(String code) {
        // Check if input code is null or empty
        if (code == null || code.isEmpty()) {
            // Return false for null or empty input
            return false;
        }

        // Normalize the code for case-insensitive comparison
        // Convert to uppercase and trim whitespace
        String normalizedCode = code.toUpperCase().trim();

        // Iterate through all existing students
        for (Student s : students) {
            // Check if current student's code matches the normalized search code
            // Also check if student's code is not null
            if (s.getCode() != null && s.getCode().toUpperCase().trim().equals(normalizedCode)) {
                // Return true if duplicate found
                return true;  // Found duplicate
            }
        }
        // Return false if no duplicate found
        return false;  // No duplicate found
    }

    /**
     * Searches for students by name (case-insensitive partial match).
     * Returns all students if keyword is empty.
     *
     * @param keyword the name to search for
     * @return list of matching students
     */
    public List<Student> searchByName(String keyword) {
        // Check if search keyword is null or empty (after trimming)
        if (keyword == null || keyword.trim().isEmpty()) {  // Check if search keyword is empty
            // Return a copy of all students if no search keyword
            return new ArrayList<>(students);  // Return copy of all students
        }
        // Create new list to store search results
        List<Student> result = new ArrayList<>();  // Create list for search results
        // Normalize keyword for case-insensitive search (convert to lowercase and trim)
        String key = keyword.toLowerCase().trim();  // Convert keyword to lowercase and trim for case-insensitive search
        // Iterate through all students
        for (Student s : students) {  // Iterate through all students
            // Check if student name contains the keyword (case-insensitive)
            // Also check if student name is not null
            if (s.getName() != null && s.getName().toLowerCase().contains(key)) {  // Check if name contains keyword
                // Add matching student to results list
                result.add(s);  // Add matching student to results
            }
        }
        // Return the search results
        return result;  // Return search results
    }

    /**
     * Returns all students sorted by name.
     *
     * @return sorted list of all students
     */
    public List<Student> getAllStudents() {
        // Create a copy of the student list (to avoid modifying original)
        List<Student> sorted = new ArrayList<>(students);  // Create copy of student list
        // Sort the copy using Student's compareTo method (natural ordering by name)
        // Method reference syntax: Student::compareTo
        sorted.sort(Student::compareTo);  // Sort using Student's natural ordering (by name)
        // Return the sorted list
        return sorted;  // Return sorted list
    }

    /**
     * Gets a student by code (for testing or internal use).
     *
     * @param code the student code to find
     * @return the student with matching code, or null if not found
     */
    public Student getStudentByCode(String code) {
        // Check if input code is null or empty
        if (code == null || code.isEmpty()) {
            // Return null for invalid input
            return null;
        }

        // Normalize the code for case-insensitive comparison
        // Convert to uppercase and trim whitespace
        String normalizedCode = code.toUpperCase().trim();

        // Iterate through all students
        for (Student s : students) {
            // Check if current student's code matches the search code
            // Also check if student's code is not null
            if (s.getCode() != null && s.getCode().toUpperCase().trim().equals(normalizedCode)) {
                // Return the matching student
                return s;
            }
        }
        // Return null if no student found with the given code
        return null;
    }
}