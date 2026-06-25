import view.LoginView;

/**
 * S04 - Write a login function uses MD5 encryption for passwords (separate from FPT Webmail software Project
 * Main application entry point.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class Main {
    /**
     * Main method to launch the application.
     * Creates and starts the LoginView.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create a new LoginView instance and start the application
        new LoginView().start();
    }
}