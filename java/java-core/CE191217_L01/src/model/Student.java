package model;

/**
 * L01 -Create a Java console program to manage students
 * Represents a Student entity in the Student Management System.
 * Contains student identification, personal information, and course enrollment.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Student {
    // Declare a private String variable to store the student's unique ID
    private String id;
    // Declare a private String variable to store the student's full name
    private String name;
    // Declare a private String variable to store the student's enrolled course
    private String course;

    /**
     * Constructs a new Student with the specified details.
     *
     * @param id     the student's unique identifier
     * @param name   the student's full name
     * @param course the course the student is enrolled in
     */
    public Student(String id, String name, String course) {
        // Assign the id parameter value to the instance variable
        this.id = id;
        // Assign the name parameter value to the instance variable
        this.name = name;
        // Assign the course parameter value to the instance variable
        this.course = course;
    }

    // Getters and setters

    /**
     * @return the student's ID
     */
    public String getId() {
        // Return the id value
        return id;
    }

    /**
     * Updates the student's ID.
     *
     * @param id the new ID to set
     */
    public void setId(String id) {
        // Assign the new id value to the instance variable
        this.id = id;
    }

    /**
     * @return the student's name
     */
    public String getName() {
        // Return the name value
        return name;
    }

    /**
     * Updates the student's name.
     *
     * @param name the new name to set
     */
    public void setName(String name) {
        // Assign the new name value to the instance variable
        this.name = name;
    }

    /**
     * @return the student's course
     */
    public String getCourse() {
        // Return the course value
        return course;
    }

    /**
     * Updates the student's course.
     *
     * @param course the new course to set
     */
    public void setCourse(String course) {
        // Assign the new course value to the instance variable
        this.course = course;
    }

    /**
     * Returns a formatted string representation of the student.
     * Format: "ID: [id], Name: [name], Course: [course]"
     *
     * @return string representation of the student
     */
    @Override
    public String toString() {
        // Return formatted string with all student information
        return "ID: " + id + ", Name: " + name + ", Course: " + course;
    }
}