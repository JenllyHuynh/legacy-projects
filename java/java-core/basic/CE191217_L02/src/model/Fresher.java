package model;

/**
 * L02 - Create a Java console program to manage Candidates of company
 * Fresher Candidate class
 * Represents fresh graduate candidates without work experience
 * Additional attributes: GraduationDate, GraduationRank, Education
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Fresher extends Candidate {
    // Declare a private String variable to store graduation date in "YYYY-MM" or "YYYY" format
    private String graduationDate;
    // Declare a private String variable to store graduation rank (Excellence, Good, Fair, Poor)
    private String graduationRank;
    // Declare a private String variable to store university/education institution name
    private String education;

    /**
     * Constructs a new Fresher candidate with the specified details.
     *
     * @param candidateId    the candidate's unique identifier
     * @param firstName      the candidate's first name
     * @param lastName       the candidate's last name
     * @param birthDate      the candidate's birth year in "YYYY" format
     * @param address        the candidate's address
     * @param phone          the candidate's phone number
     * @param email          the candidate's email address
     * @param graduationDate graduation date in "YYYY-MM" or "YYYY" format
     * @param graduationRank graduation rank (Excellence, Good, Fair, Poor)
     * @param education      university/education institution name
     */
    public Fresher(String candidateId, String firstName, String lastName,
                   String birthDate, String address, String phone,
                   String email, String graduationDate, String graduationRank,
                   String education) {
        // Call parent constructor with Fresher type constant
        super(candidateId, firstName, lastName, birthDate, address, phone,
                email, Candidate.TYPE_FRESHER);
        // Assign the graduationDate parameter value to the instance variable
        this.graduationDate = graduationDate;
        // Assign the graduationRank parameter value to the instance variable
        this.graduationRank = graduationRank;
        // Assign the education parameter value to the instance variable
        this.education = education;
    }

    /**
     * Retrieves the candidate's graduation date.
     *
     * @return the graduation date in "YYYY-MM" or "YYYY" format
     */
    public String getGraduationDate() {
        // Return the graduationDate value
        return graduationDate;
    }

    /**
     * Updates the candidate's graduation date.
     *
     * @param graduationDate the new graduation date in "YYYY-MM" or "YYYY" format
     */
    public void setGraduationDate(String graduationDate) {
        // Assign the new graduationDate value to the instance variable
        this.graduationDate = graduationDate;
    }

    /**
     * Retrieves the candidate's graduation rank.
     *
     * @return the graduation rank (Excellence, Good, Fair, Poor)
     */
    public String getGraduationRank() {
        // Return the graduationRank value
        return graduationRank;
    }

    /**
     * Updates the candidate's graduation rank.
     *
     * @param graduationRank the new graduation rank (Excellence, Good, Fair, Poor)
     */
    public void setGraduationRank(String graduationRank) {
        // Assign the new graduationRank value to the instance variable
        this.graduationRank = graduationRank;
    }

    /**
     * Retrieves the name of the candidate's university or educational institution.
     *
     * @return the university or education institution name
     */
    public String getEducation() {
        // Return the education value
        return education;
    }

    /**
     * Updates the education institution.
     *
     * @param education the new university/education institution name
     */
    public void setEducation(String education) {
        // Assign the new education value to the instance variable
        this.education = education;
    }

    /**
     * Implementation of abstract method from Candidate class.
     * Provides additional information specific to Fresher candidates.
     *
     * @return formatted string with graduation date, rank, and university
     */
    @Override
    public String getAdditionalInfo() {
        // Return formatted string with graduation details
        return String.format("Graduated: %s | Rank: %s | University: %s",
                graduationDate, graduationRank, education);
    }

    /**
     * Returns a formatted string representation of the Fresher candidate.
     * Combines parent toString() with additional fresher information.
     *
     * @return formatted candidate information including fresher details
     */
    @Override
    public String toString() {
        // Combine parent's toString with additional fresher information
        return super.toString() + " | " + getAdditionalInfo();
    }
}