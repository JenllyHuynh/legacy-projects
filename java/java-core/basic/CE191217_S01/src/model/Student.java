package model;

/**
 * S01 Manage student
 * Represents a Student entity in the system.
 * Implements Comparable for natural ordering by student name.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Student implements Comparable<Student> {
    // Declare a private String variable to store the student's unique identification code
    private String code;
    // Declare a private String variable to store the student's full name
    private String name;
    // Declare a private String variable to store the student's date of birth in string format
    private String dob;
    // Declare a private double variable to store the student's learning point (GPA)
    private double learningPoint;

    /**
     * Constructs a new Student with the specified details.
     *
     * @param code          the student's unique code
     * @param name          the student's full name (automatically trimmed)
     * @param dob           the student's date of birth
     * @param learningPoint the student's learning point
     */
    public Student(String code, String name, String dob, double learningPoint) {
        // Clean and validate input
        // If code is not null, trim whitespace and replace multiple spaces with single space; otherwise assign empty string
        this.code = (code != null) ? code.trim().replaceAll("\\s+", "") : "";
        // If name is not null, trim whitespace and replace multiple spaces with single space; otherwise assign empty string
        this.name = (name != null) ? name.trim().replaceAll("\\s+", " ") : "";
        // If dob is not null, trim whitespace and replace multiple spaces with single space; otherwise assign empty string
        this.dob = (dob != null) ? dob.trim().replaceAll("\\s+", "") : "";
        // Assign the learningPoint parameter value to the instance variable
        this.learningPoint = learningPoint;
    }

    /**
     * @return the student's code
     */
    public String getCode() {
        // Return the code if it's not null, otherwise return an empty string
        return code != null ? code : "";
    }

    /**
     * @return the student's name
     */
    public String getName() {
        // Return the name if it's not null, otherwise return an empty string
        return name != null ? name : "";
    }

    /**
     * @return the student's date of birth
     */
    public String getDob() {
        // Return the dob if it's not null, otherwise return an empty string
        return dob != null ? dob : "";
    }

    /**
     * @return the student's learning point
     */
    public double getLearningPoint() {
        // Return the learningPoint value
        return learningPoint;
    }

    /**
     * Returns a formatted string representation of the student for display.
     *
     * @return formatted student information
     */
    @Override
    public String toString() {
        // Return a formatted string containing all student information with labels and newlines
        return "Student code: " + getCode() + "\n" +
                "Student name: " + getName() + "\n" +
                "Date of birth: " + getDob() + "\n" +
                "Learning point: " + String.format("%.1f", getLearningPoint());  // Format to 1 decimal place
    }

    /**
     * Converts student data to a string format suitable for file storage.
     * Uses '#' as delimiter between fields.
     *
     * @return student data as file-ready string
     */
    public String toFileString() {
        // Return a string with all fields separated by '#' delimiter
        return getCode() + "#" + getName() + "#" + getDob() + "#" + getLearningPoint();
    }

    /**
     * Compares this student to another student by name for sorting.
     * Comparison is case-insensitive.
     *
     * @param o the other student to compare to
     * @return negative, zero, or positive if this student's name comes before,
     * equal to, or after the other student's name alphabetically
     */
    @Override
    public int compareTo(Student o) {
        // Check if both names are null, return 0 (equal)
        if (this.getName() == null && o.getName() == null) return 0;
        // Check if this name is null, return -1 (this comes before)
        if (this.getName() == null) return -1;
        // Check if the other name is null, return 1 (this comes after)
        if (o.getName() == null) return 1;

//        return this.getName().compareToIgnoreCase(o.getName());  // Case-insensitive name comparison

        // Compare the length of this object's name with the other object's name
        int lengthCompare = Integer.compare(this.getName().length(), o.getName().length());
        // If both names have the same length
        if (lengthCompare == 0) {
            // Compare the names alphabetically, ignoring case differences
            return this.getName().compareToIgnoreCase(o.getName());
        }
        // If lengths are different, return the comparison result based on length
        return lengthCompare;
    }
}