package model;

/**
 * L02 - Create a Java console program to manage Candidates of company
 * Experience Candidate class
 * Represents candidates with work experience
 * Additional attributes: ExpInYear, ProSkill
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Experience extends Candidate {
    // Declare a private int variable to store years of experience (range 0-100)
    private int expInYear;
    // Declare a private String variable to store professional skill
    private String proSkill;

    /**
     * Constructs a new Experience candidate with the specified details.
     *
     * @param candidateId the candidate's unique identifier
     * @param firstName   the candidate's first name
     * @param lastName    the candidate's last name
     * @param birthDate   the candidate's birth year in "YYYY" format
     * @param address     the candidate's address
     * @param phone       the candidate's phone number
     * @param email       the candidate's email address
     * @param expInYear   years of experience (0-100)
     * @param proSkill    professional skill description
     */
    public Experience(String candidateId, String firstName, String lastName,
                      String birthDate, String address, String phone,
                      String email, int expInYear, String proSkill) {
        // Call parent constructor with Experience type constant
        super(candidateId, firstName, lastName, birthDate, address, phone,
                email, Candidate.TYPE_EXPERIENCE);
        // Assign the expInYear parameter value to the instance variable
        this.expInYear = expInYear;
        // Assign the proSkill parameter value to the instance variable
        this.proSkill = proSkill;
    }

    /**
     * Retrieves the candidate's total years of professional experience.
     *
     * @return the number of years of experience
     */
    public int getExpInYear() {
        // Return the expInYear value
        return expInYear;
    }

    /**
     * Updates the candidate's years of professional experience.
     *
     * @param expInYear the new number of years of experience (valid range: 0–100)
     */
    public void setExpInYear(int expInYear) {
        // Assign the new expInYear value to the instance variable
        this.expInYear = expInYear;
    }

    /**
     * Retrieves the candidate's primary professional skill.
     *
     * @return the professional skill
     */
    public String getProSkill() {
        // Return the proSkill value
        return proSkill;
    }


    /**
     * Updates the professional skill.
     *
     * @param proSkill the new professional skill description
     */
    public void setProSkill(String proSkill) {
        // Assign the new proSkill value to the instance variable
        this.proSkill = proSkill;
    }

    /**
     * Implementation of abstract method from Candidate class.
     * Provides additional information specific to Experience candidates.
     *
     * @return formatted string with experience years and professional skill
     */
    @Override
    public String getAdditionalInfo() {
        // Return formatted string with experience years and skill
        return String.format("Experience: %d years | Skill: %s", expInYear, proSkill);
    }

    /**
     * Returns a formatted string representation of the Experience candidate.
     * Combines parent toString() with additional experience information.
     *
     * @return formatted candidate information including experience details
     */
    @Override
    public String toString() {
        // Combine parent's toString with additional experience information
        return super.toString() + " | " + getAdditionalInfo();
    }
}