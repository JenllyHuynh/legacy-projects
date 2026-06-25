package model;

/**
 * L02 - Create a Java console program to manage Candidates of company
 * Intern Candidate class
 * Represents internship candidates currently studying
 * Additional attributes: Majors, Semester, UniversityName
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Intern extends Candidate {
    // Declare a private String variable to store major/field of study
    private String majors;
    // Declare a private String variable to store current semester
    private String semester;
    // Declare a private String variable to store university name
    private String universityName;

    /**
     * Constructs a new Intern candidate with the specified details.
     *
     * @param candidateId    the candidate's unique identifier
     * @param firstName      the candidate's first name
     * @param lastName       the candidate's last name
     * @param birthDate      the candidate's birth year in "YYYY" format
     * @param address        the candidate's address
     * @param phone          the candidate's phone number
     * @param email          the candidate's email address
     * @param majors         major/field of study
     * @param semester       current semester
     * @param universityName university name
     */
    public Intern(String candidateId, String firstName, String lastName,
                  String birthDate, String address, String phone,
                  String email, String majors, String semester,
                  String universityName) {
        // Call parent constructor with Intern type constant
        super(candidateId, firstName, lastName, birthDate, address, phone,
                email, Candidate.TYPE_INTERN);
        // Assign the majors parameter value to the instance variable
        this.majors = majors;
        // Assign the semester parameter value to the instance variable
        this.semester = semester;
        // Assign the universityName parameter value to the instance variable
        this.universityName = universityName;
    }

    /**
     * Retrieves the candidate's major or field of study.
     *
     * @return the major or field of study
     */
    public String getMajors() {
        // Return the majors value
        return majors;
    }

    /**
     * Updates the candidate's major or field of study.
     *
     * @param majors the new major or field of study
     */
    public void setMajors(String majors) {
        // Assign the new majors value to the instance variable
        this.majors = majors;
    }

    /**
     * Retrieves the candidate's current semester.
     *
     * @return the current semester
     */
    public String getSemester() {
        // Return the semester value
        return semester;
    }

    /**
     * Updates the candidate's current semester.
     *
     * @param semester the new current semester
     */
    public void setSemester(String semester) {
        // Assign the new semester value to the instance variable
        this.semester = semester;
    }

    /**
     * Retrieves the name of the candidate's university.
     *
     * @return the university name
     */
    public String getUniversityName() {
        // Return the universityName value
        return universityName;
    }


    /**
     * Updates the university name.
     *
     * @param universityName the new university name
     */
    public void setUniversityName(String universityName) {
        // Assign the new universityName value to the instance variable
        this.universityName = universityName;
    }

    /**
     * Implementation of abstract method from Candidate class.
     * Provides additional information specific to Intern candidates.
     *
     * @return formatted string with major, semester, and university
     */
    @Override
    public String getAdditionalInfo() {
        // Return formatted string with intern study details
        return String.format("Major: %s | Semester: %s | University: %s",
                majors, semester, universityName);
    }

    /**
     * Returns a formatted string representation of the Intern candidate.
     * Combines parent toString() with additional intern information.
     *
     * @return formatted candidate information including intern details
     */
    @Override
    public String toString() {
        // Combine parent's toString with additional intern information
        return super.toString() + " | " + getAdditionalInfo();
    }
}