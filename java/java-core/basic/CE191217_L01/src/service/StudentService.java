package service;

import model.Student;
import util.InputUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing student operations.
 * Provides CRUD operations, searching, sorting, and reporting functionality.
 * Author: [Your Name Here]
 */
public class StudentService {
    // Declare a private final List to store all student records
    private final List<Student> students = new ArrayList<>();

    // Declare a static Scanner instance for reading input in update methods
    private static final java.util.Scanner scanner = new java.util.Scanner(System.in);

    /**
     * L01 -Create a Java console program to manage students
     * Creates students with minimum requirement of 10 students.
     * Guides user through creating at least 10 students and optionally more.
     * Author: Huynh Kim Hue CE191217 28/11/2025
     */
    public void createStudents() {
        // Display creation header
        System.out.println("\n=== CREATE STUDENTS ===");
        System.out.println("You need to create at least 10 students.");

        // Create minimum 10 students
        while (students.size() < 10) {
            // Display current student number being created
            System.out.println("\n--- Student #" + (students.size() + 1) + " ---");
            // Add a single student
            addStudent();
        }

        // Display completion message
        System.out.println("\nMinimum 10 students created successfully.");

        // Ask if user wants to add more students
        while (true) {
            System.out.print("\nDo you want to add more students (Y/N)? ");
            // Check if user wants to continue adding
            if (!InputUtils.readContinueYN()) {
                break;
            }
            // Add additional student
            System.out.println("\n--- Add more student ---");
            addStudent();
        }
    }

    /**
     * Adds a single student with validation.
     * Reads input, checks for duplicate ID, and creates student record.
     */
    private void addStudent() {
        // Read student ID from input
        String id = InputUtils.readId();

        // Check for duplicate ID
        if (findById(id) != null) {
            System.out.println("ID '" + id + "' already exists! Please use a different ID.");
            // Exit if duplicate found
            return;
        }

        // Read student name from input
        String name = InputUtils.readName();
        // Read student course from input
        String course = InputUtils.readCourse();

        // Create and add new student to list
        students.add(new Student(id, name, course));
        // Display success message
        System.out.println("Student '" + name + "' added successfully.");
    }

    /**
     * Finds students by name (partial match) and sorts results alphabetically.
     * Search is case-insensitive and displays results in formatted table.
     */
    public void findAndSort() {
        // Display search header
        System.out.println("\n=== FIND AND SORT ===");

        // Check if student list is empty
        if (students.isEmpty()) {
            System.out.println("No students in the system.");
            return;
        }

        // Read search name from input
        String searchName = InputUtils.readSearchName();

        // List to store found students
        List<Student> found = new ArrayList<>();
        // Search through all students
        for (Student s : students) {
            // Case-insensitive partial name match
            if (s.getName().toLowerCase().contains(searchName.toLowerCase())) {
                found.add(s);
            }
        }

        // Check if any students found
        if (found.isEmpty()) {
            System.out.println("No students found with name containing: '" + searchName + "'");
            return;
        }

        // Sort found students by name alphabetically
        Collections.sort(found, Comparator.comparing(Student::getName));

        // Display results header
        System.out.println("\nFound " + found.size() + " student(s):");
        System.out.println("+----------+----------------------+----------+");
        System.out.println("| ID       | Name                 | Course   |");
        System.out.println("+----------+----------------------+----------+");

        // Display each found student in table format
        for (Student s : found) {
            System.out.printf("| %-8s | %-20s | %-8s |\n",
                    s.getId(), s.getName(), s.getCourse());
        }
        // Display table footer
        System.out.println("+----------+----------------------+----------+");
    }

    /**
     * Updates or deletes a student by ID.
     * Finds student by ID, displays current info, and performs update or delete operation.
     */
    public void updateOrDelete() {
        // Display action header
        System.out.println("\n=== UPDATE / DELETE ===");

        // Check if student list is empty
        if (students.isEmpty()) {
            System.out.println("No students in the system.");
            return;
        }

        // Read ID for action
        String id = InputUtils.readIdForAction();
        // Find student by ID
        Student student = findById(id);

        // Check if student exists
        if (student == null) {
            System.out.println("Student with ID '" + id + "' not found.");
            return;
        }

        // Display current student information
        System.out.println("\nFound student:");
        System.out.println("ID: " + student.getId());
        System.out.println("Name: " + student.getName());
        System.out.println("Course: " + student.getCourse());

        // Read action choice (Update or Delete)
        char action = InputUtils.readUD();

        // Handle delete action
        if (action == 'D') {
            // Remove student from list
            students.remove(student);
            System.out.println("Student '" + student.getName() + "' deleted successfully.");
        } else {
            // Handle update action
            System.out.println("\n--- Update Student Information ---");

            // Update name (optional)
            System.out.println("Current name: " + student.getName());
            System.out.print("New name (press Enter to keep current): ");
            String newName = scanner.nextLine().trim();
            if (!newName.isEmpty()) {
                student.setName(newName);
            }

            // Update course (required)
            System.out.println("Current course: " + student.getCourse());
            String newCourse = InputUtils.readCourse();
            student.setCourse(newCourse);

            // Display success message
            System.out.println("Student information updated successfully.");
        }
    }

    /**
     * Generates a report showing students with their course counts.
     * Creates summary statistics and displays in formatted table.
     */
    public void report() {
        // Display report header
        System.out.println("\n=== STUDENT REPORT ===");

        // Check if student list is empty
        if (students.isEmpty()) {
            System.out.println("No students to report.");
            return;
        }

        // Create report map structure: Name -> (Course -> Count)
        Map<String, Map<String, Integer>> reportMap = new HashMap<>();

        // Process all students
        for (Student s : students) {
            String name = s.getName();
            String course = s.getCourse();

            // Initialize map for new student names
            reportMap.putIfAbsent(name, new HashMap<>());
            Map<String, Integer> courseMap = reportMap.get(name);
            // Increment course count
            courseMap.put(course, courseMap.getOrDefault(course, 0) + 1);
        }

        // Sort student names alphabetically
        List<String> sortedNames = new ArrayList<>(reportMap.keySet());
        Collections.sort(sortedNames);

        // Prepare table rows for display
        List<Object[]> rows = new ArrayList<>();
        int index = 1;

        // Process each student and their courses
        for (String name : sortedNames) {
            Map<String, Integer> courseMap = reportMap.get(name);
            List<String> sortedCourses = new ArrayList<>(courseMap.keySet());
            Collections.sort(sortedCourses);

            // Create row for each course per student
            for (String course : sortedCourses) {
                rows.add(new Object[]{index++, name, course, courseMap.get(course)});
            }
        }

        // Display report table header
        System.out.println("+-----+----------------------+----------+-----------------+");
        System.out.println("| No. | Student name         | Course   | Total of course |");
        System.out.println("+-----+----------------------+----------+-----------------+");

        // Display each row of the report
        for (Object[] row : rows) {
            System.out.printf("| %3d | %-20s | %-8s | %15d |\n",
                    row[0], row[1], row[2], row[3]);
        }

        // Display report table footer and summary statistics
        System.out.println("+-----+----------------------+----------+-----------------+");
        System.out.println("Total unique students: " + sortedNames.size());
        System.out.println("Total records: " + students.size());
    }

    /**
     * Finds a student by ID (case-insensitive search).
     *
     * @param id the ID to search for
     * @return the Student object if found, null otherwise
     */
    private Student findById(String id) {
        // Iterate through all students
        for (Student s : students) {
            // Case-insensitive ID comparison
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        // Student not found
        return null;
    }

    /**
     * Checks if the student list is empty.
     *
     * @return true if no students exist, false otherwise
     */
    public boolean isEmpty() {
        // Return emptiness status of students list
        return students.isEmpty();
    }
}