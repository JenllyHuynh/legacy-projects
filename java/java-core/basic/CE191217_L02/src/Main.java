import view.CandidateView;

/**
 * L02 - Create a Java console program to manage Candidates of company
 * Main application entry point for Candidate Management System.
 * Launches the candidate management interface.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Main {
    /**
     * Main method to launch the Candidate Management System.
     * Creates and starts the CandidateView.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create a new CandidateView instance and start the application
        new CandidateView().start();
    }
}