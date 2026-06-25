package service;

import model.*;
import util.InputUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * L02 - Create a Java console program to manage Candidates of company
 * Service class for managing candidate operations.
 * Provides CRUD operations, searching, and categorization by candidate type.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class CandidateService {
    // Declare separate lists for each candidate type
    private final List<Experience> experiences = new ArrayList<>();
    private final List<Fresher> freshers = new ArrayList<>();
    private final List<Intern> interns = new ArrayList<>();
    // Declare a combined list for all candidates
    private final List<Candidate> allCandidates = new ArrayList<>();

    /**
     * Creates a new Experience candidate.
     * Validates ID uniqueness and collects candidate information.
     */
    public void createExperience() {
        // Display creation header
        System.out.println("\n=== CREATE EXPERIENCE CANDIDATE ===");
        // Read candidate ID
        String id = InputUtils.readCandidateId().toUpperCase();

        // Check for duplicate ID
        if (findCandidateById(id) != null) {
            System.out.println("ID '" + id + "' already exists!");
            return;
        }

        // Create new Experience candidate with input data
        Experience exp = new Experience(
                // Pass the 'id' of the experienced candidate
                id,
                // Read the candidate's first name from input
                InputUtils.readFirstName(),
                // Read the candidate's last name from input
                InputUtils.readLastName(),
                // Read the candidate's birth date from input
                InputUtils.readBirthDate(),
                // Read the candidate's address from input
                InputUtils.readAddress(),
                // Read the candidate's phone number from input
                InputUtils.readPhone(),
                // Read the candidate's email address from input
                InputUtils.readEmail(),
                // Read the candidate's total years of experience from input
                InputUtils.readExperienceYears(),
                // Read the candidate's professional skill (e.g., Java, Project Management) from input
                InputUtils.readProSkill()
        );


        // Add to both specific and combined lists
        experiences.add(exp);
        allCandidates.add(exp);
        // Display success message
        System.out.println("Experience candidate created!");
    }

    /**
     * Creates a new Fresher candidate.
     * Validates ID uniqueness and collects candidate information.
     */
    public void createFresher() {
        // Display creation header
        System.out.println("\n=== CREATE FRESHER CANDIDATE ===");
        // Read candidate ID
        String id = InputUtils.readCandidateId().toUpperCase();

        // Check for duplicate ID
        if (findCandidateById(id) != null) {
            System.out.println("ID '" + id + "' already exists!");
            return;
        }

        // Create new Fresher candidate with input data
        Fresher fresher = new Fresher(
                // Pass the 'id' of the fresher
                id,
                // Read the fresher's first name from input
                InputUtils.readFirstName(),
                // Read the fresher's last name from input
                InputUtils.readLastName(),
                // Read the fresher's birth date from input
                InputUtils.readBirthDate(),
                // Read the fresher's address from input
                InputUtils.readAddress(),
                // Read the fresher's phone number from input
                InputUtils.readPhone(),
                // Read the fresher's email address from input
                InputUtils.readEmail(),
                // Read the fresher's graduation date from input
                InputUtils.readGraduationDate(),
                // Read the fresher's graduation rank (e.g., Excellent, Good, Average) from input
                InputUtils.readGraduationRank(),
                // Read the fresher's university name from input
                InputUtils.readUniversity()
        );


        // Add to both specific and combined lists
        freshers.add(fresher);
        allCandidates.add(fresher);
        // Display success message
        System.out.println("Fresher candidate created!");
    }

    /**
     * Creates a new Intern candidate.
     * Validates ID uniqueness and collects candidate information.
     */
    public void createIntern() {
        // Display creation header
        System.out.println("\n=== CREATE INTERN CANDIDATE ===");
        // Read candidate ID
        String id = InputUtils.readCandidateId().toUpperCase();

        // Check for duplicate ID
        if (findCandidateById(id) != null) {
            System.out.println("ID '" + id + "' already exists!");
            return;
        }

        // Create new Intern candidate with input data
        Intern intern = new Intern(
                // Pass the 'id' of the intern
                id,
                // Read the intern's first name from input
                InputUtils.readFirstName(),
                // Read the intern's last name from input
                InputUtils.readLastName(),
                // Read the intern's birth date from input
                InputUtils.readBirthDate(),
                // Read the intern's address from input
                InputUtils.readAddress(),
                // Read the intern's phone number from input
                InputUtils.readPhone(),
                // Read the intern's email address from input
                InputUtils.readEmail(),
                // Read the intern's majors (field of study) from input
                InputUtils.readMajors(),
                // Read the intern's current semester from input
                InputUtils.readSemester(),
                // Read the intern's university name from input
                InputUtils.readUniversity()
        );


        // Add to both specific and combined lists
        interns.add(intern);
        allCandidates.add(intern);
        // Display success message
        System.out.println("Intern candidate created!");
    }

    /**
     * Searches candidates by name and type.
     * Displays search results in formatted table.
     */
    public void searchCandidates() {
        // Display search header
        System.out.println("\n=== SEARCH CANDIDATES ===");

        //check list
        if (allCandidates.isEmpty()){
            System.out.println("No candidates in system. Cannot search!");
            return;
        }

        // Display all candidates summary
        displayAllCandidates();

        // Get search criteria
        String searchName = InputUtils.readSearchName();
        //int searchType = InputUtils.readCandidateType();

        // Search for matching candidates
        List<Candidate> results = new ArrayList<>();
        for (Candidate c : allCandidates) {
            // Check name match (case-insensitive, partial match)
            boolean nameMatch = c.getFirstName().toLowerCase().contains(searchName.toLowerCase()) ||
                    c.getLastName().toLowerCase().contains(searchName.toLowerCase());

            // Check type match
            //boolean typeMatch = c.getCandidateType() == searchType;

            // Add to results if both criteria match
            if (nameMatch) results.add(c);
        }

        // Display search results
        if (results.isEmpty()) {
            System.out.println("\nNo candidates found!");
        } else {
            System.out.println("\n=== SEARCH RESULTS (" + results.size() + " found) ===");
            // Display table header
            System.out.println("+-----+----------------------+----------+----------------------+--------------+----------------------+------+");
            System.out.println("| No. | Full Name            | BirthYear| Address              | Phone        | Email                | Type |");
            System.out.println("+-----+----------------------+----------+----------------------+--------------+----------------------+------+");

            // Display each result row
            for (int i = 0; i < results.size(); i++) {
                Candidate c = results.get(i); // Get the Candidate object at index 'i' from the list
                // Print formatted output in table style
                System.out.printf("| %3d | %-20s | %-8s | %-20s | %-12s | %-20s | %-4s |\n",
                        i + 1, c.getFullName(), c.getBirthDate(),
                        c.getAddress(), c.getPhone(), c.getEmail(),
                        c.getCandidateType());
            }
            // Display table footer
            System.out.println("+-----+----------------------+----------+----------------------+--------------+----------------------+------+");
        }
    }

    /**
     * Displays summary of all candidates grouped by type.
     */
    private void displayAllCandidates() {
        // Check if any candidates exist
        if (allCandidates.isEmpty()) {
            System.out.println("No candidates in system.");
            return;
        }

        // Display Experience candidates if any exist
        if (!experiences.isEmpty()) {
            System.out.println("\n========= EXPERIENCE CANDIDATE ================");
            for (Experience exp : experiences) System.out.println(exp.getCandidateId() + "-" + exp.getFullName() + "\n");
        }

        // Display Fresher candidates if any exist
        if (!freshers.isEmpty()) {
            System.out.println("\n========= FRESHER CANDIDATE ================");
            for (Fresher fr : freshers) System.out.println(fr.getCandidateId() + "-" + fr.getFullName() + "\n");
        }

        // Display Intern candidates if any exist
        if (!interns.isEmpty()) {
            System.out.println("\n========= INTERN CANDIDATE ================");
            for (Intern in : interns) System.out.println(in.getCandidateId() +"-" + in.getFullName() + "\n");
        }
    }

    /**
     * Finds a candidate by ID (case-insensitive search).
     *
     * @param id the candidate ID to search for
     * @return the Candidate object if found, null otherwise
     */
    private Candidate findCandidateById(String id) {
        // Iterate through all candidates
        for (Candidate c : allCandidates) {
            // Case-insensitive ID comparison
            if (c.getCandidateId().equalsIgnoreCase(id)) return c;
        }
        // Candidate not found
        return null;
    }
}