package model;

/**
 * L02 - Create a Java console program to manage Candidates of company
 * Abstract base class for all candidate types
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public abstract class Candidate {
    // Declare a private String variable to store the candidate's unique identifier
    private String candidateId;
    // Declare a private String variable to store the candidate's first name
    private String firstName;
    // Declare a private String variable to store the candidate's last name
    private String lastName;
    // Declare a private String variable to store the candidate's birth year in "YYYY" format
    private String birthDate;
    // Declare a private String variable to store the candidate's address
    private String address;
    // Declare a private String variable to store the candidate's phone number
    private String phone;
    // Declare a private String variable to store the candidate's email address
    private String email;
    // Declare a private int variable to store the candidate type (0: Experience, 1: Fresher, 2: Intern)
    private int candidateType;


    /**
     * Constant for Experience candidate type
     */
    public static final int TYPE_EXPERIENCE = 0;
    /**
     * Constant for Fresher candidate type
     */
    public static final int TYPE_FRESHER = 1;
    /**
     * Constant for Intern candidate type
     */
    public static final int TYPE_INTERN = 2;

    /**
     * Constructs a new Candidate with the specified details.
     *
     * @param candidateId   the candidate's unique identifier
     * @param firstName     the candidate's first name
     * @param lastName      the candidate's last name
     * @param birthDate     the candidate's birth year in "YYYY" format
     * @param address       the candidate's address
     * @param phone         the candidate's phone number
     * @param email         the candidate's email address
     * @param candidateType the candidate type (0: Experience, 1: Fresher, 2: Intern)
     */
    public Candidate(String candidateId, String firstName, String lastName, String birthDate, String address, String phone, String email, int candidateType) {
        // Assign the candidateId parameter value to the instance variable
        this.candidateId = candidateId;
        // Assign the firstName parameter value to the instance variable
        this.firstName = firstName;
        // Assign the lastName parameter value to the instance variable
        this.lastName = lastName;
        // Assign the birthDate parameter value to the instance variable
        this.birthDate = birthDate;
        // Assign the address parameter value to the instance variable
        this.address = address;
        // Assign the phone parameter value to the instance variable
        this.phone = phone;
        // Assign the email parameter value to the instance variable
        this.email = email;
        // Assign the candidateType parameter value to the instance variable
        this.candidateType = candidateType;
    }

    /**
     * Retrieves the candidate's unique identifier.
     *
     * @return the candidate's ID
     */
    public String getCandidateId() {
        // Return the candidateId value
        return candidateId;
    }

    /**
     * Retrieves the candidate's given name.
     *
     * @return the candidate's first name
     */
    public String getFirstName() {
        // Return the firstName value
        return firstName;
    }

    /**
     * Retrieves the candidate's family name.
     *
     * @return the candidate's last name
     */
    public String getLastName() {
        // Return the lastName value
        return lastName;
    }

    /**
     * Retrieves the candidate's year of birth.
     *
     * @return the candidate's birth year in "YYYY" format
     */
    public String getBirthDate() {
        // Return the birthDate value
        return birthDate;
    }

    /**
     * Retrieves the candidate's residential address.
     *
     * @return the candidate's address
     */
    public String getAddress() {
        // Return the address value
        return address;
    }

    /**
     * Retrieves the candidate's contact phone number.
     *
     * @return the candidate's phone number
     */
    public String getPhone() {
        // Return the phone value
        return phone;
    }

    /**
     * Retrieves the candidate's email contact information.
     *
     * @return the candidate's email address
     */
    public String getEmail() {
        // Return the email value
        return email;
    }

    /**
     * Retrieves the candidate's type classification.
     *
     * @return the candidate type (0: Experience, 1: Fresher, 2: Intern)
     */
    public int getCandidateType() {
        // Return the candidateType value
        return candidateType;
    }


    /**
     * Updates the candidate's ID.
     *
     * @param candidateId the new candidate ID
     */
    public void setCandidateId(String candidateId) {
        // Assign the new candidateId value to the instance variable
        this.candidateId = candidateId;
    }

    /**
     * Updates the candidate's first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        // Assign the new firstName value to the instance variable
        this.firstName = firstName;
    }

    /**
     * Updates the candidate's last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        // Assign the new lastName value to the instance variable
        this.lastName = lastName;
    }

    /**
     * Updates the candidate's birth year.
     *
     * @param birthDate the new birth year in "YYYY" format
     */
    public void setBirthDate(String birthDate) {
        // Assign the new birthDate value to the instance variable
        this.birthDate = birthDate;
    }

    /**
     * Updates the candidate's address.
     *
     * @param address the new address
     */
    public void setAddress(String address) {
        // Assign the new address value to the instance variable
        this.address = address;
    }

    /**
     * Updates the candidate's phone number.
     *
     * @param phone the new phone number
     */
    public void setPhone(String phone) {
        // Assign the new phone value to the instance variable
        this.phone = phone;
    }

    /**
     * Updates the candidate's email address.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        // Assign the new email value to the instance variable
        this.email = email;
    }

    /**
     * Updates the candidate type.
     *
     * @param candidateType the new candidate type (0: Experience, 1: Fresher, 2: Intern)
     */
    public void setCandidateType(int candidateType) {
        // Assign the new candidateType value to the instance variable
        this.candidateType = candidateType;
    }

    /**
     * Combines first name and last name to get full name.
     *
     * @return the candidate's full name (FirstName + LastName)
     */
    public String getFullName() {
        // Concatenate firstName, space, and lastName
        return firstName + " " + lastName;
    }

    /**
     * Converts numeric candidate type to descriptive string.
     *
     * @return candidate type as descriptive string
     */
    public String getCandidateTypeString() {
        // Switch on candidateType to return corresponding string
        switch (candidateType) {
            case TYPE_EXPERIENCE:
                return "Experience";
            case TYPE_FRESHER:
                return "Fresher";
            case TYPE_INTERN:
                return "Intern";
            default:
                return "Unknown";
        }
    }

    /**
     * Abstract method to get additional information specific to candidate type.
     * Each subclass must implement this method.
     *
     * @return additional information as string
     */
    public abstract String getAdditionalInfo();

    /**
     * Returns a formatted string representation of the candidate for display.
     * Includes ID, full name, type, phone, and email.
     *
     * @return formatted candidate information
     */
    @Override
    public String toString() {
        // Return formatted string with aligned columns
        return String.format("ID: %-8s | Name: %-20s | Type: %-10s | Phone: %-12s | Email: %-25s",
                candidateId, getFullName(), getCandidateTypeString(), phone, email);
    }
}