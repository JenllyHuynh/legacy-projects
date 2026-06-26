package view;

import model.User;
import service.AuthService;
import util.InputUtils;

/**
 * S04 - Write a login function uses MD5 encryption for passwords (separate from FPT Webmail software Project
 * View class for handling login interface and user interactions.
 * Provides menu-driven interface for user registration and login.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class LoginView {
    // Declare a private final AuthService instance for authentication operations
    private final AuthService authService = new AuthService();

    /**
     * Starts the login program main loop.
     * Displays menu and handles user choices until exit.
     */
    public void start() {
        // Main program loop
        while (true) {
            // Display menu options
            displayMenu();
            // Read user choice from input
            int choice = InputUtils.readChoice();

            // Handle user choice using switch statement
            switch (choice) {
                case 1 -> addUser();      // Handle user registration
                case 2 -> login();        // Handle user login
                case 3 -> {
                    // Exit the program
                    System.out.println("Goodbye!");
                    return;
                }
            }
        }
    }

    /**
     * Displays the main menu options.
     * Uses text block for formatted menu display.
     */
    private void displayMenu() {
        System.out.println("""
                ============ Login Program =========
                1. Add User
                2. Login
                3) Exit
                """);
    }

    /**
     * Handles new user registration process.
     * Collects user information and creates new account.
     */
    private void addUser() {
        // Display registration header
        System.out.println("---------- Add User --------");
        try {
            // Collect user information using InputUtils methods
            String username = InputUtils.readUsername(); // Read the username input from the user
            String password = InputUtils.readPassword(); // Read the password input from the user
            String name = InputUtils.readName();         // Read the full name input from the user
            String phone = InputUtils.readPhone();       // Read the phone number input from the user
            String email = InputUtils.readEmail();       // Read the email address input from the user
            String address = InputUtils.readAddress();   // Read the home address input from the user
            String dob = InputUtils.readDOB();           // Read the date of birth input from the user


            // Attempt to create new account
            int id = authService.addAccount(username, password, name, phone, email, address, dob);
            // Display success message with user ID
            System.out.println("Account created successfully! ID: " + id);
            // Display user information (password masked)
            System.out.println(authService.findByUsername(username));
        } catch (Exception e) {
            // Handle any exceptions during registration
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handles user login process.
     * Authenticates user and provides option to change password.
     */
    private void login() {
        // Display login header
        System.out.println("------------- Login ----------------");
        // Collect login credentials
        String username = InputUtils.readUsername();
        String password = InputUtils.readPassword();

        // Attempt authentication
        if (authService.login(username, password)) {
            // Login successful
            User user = authService.findByUsername(username);
            // Display welcome message with user's name
            System.out.printf("------------ Wellcome -----------%nHi %s, ", user.getName());
            // Ask if user wants to change password
            if (InputUtils.askChangePassword()) {
                changePassword(username);
            }
        } else {
            // Login failed
            System.out.println("Login failed! Wrong username or password.");
        }
    }

    /**
     * Handles password change process for authenticated user.
     * Verifies old password and validates new password confirmation.
     *
     * @param username the username of the user changing password
     */
    private void changePassword(String username) {
        // Prompt for old password
        System.out.print("Old password: ");
        String oldPass = InputUtils.readPassword();
        // Verify old password is correct
        if (!authService.login(username, oldPass)) {
            System.out.println("Old password incorrect!");
            return;
        }
        // Prompt for new password
        System.out.print("new password: ");
        String newPass = InputUtils.readPassword();
        // Confirm new password
        System.out.print("renew password: ");
        String renew = InputUtils.readPassword();
        // Validate password confirmation
        if (!newPass.equals(renew)) {
            System.out.println("Passwords do not match!");
            return;
        }
        // Update password in authentication service
        authService.changePassword(username, newPass);
        // Display success message
        System.out.println("Password changed successfully!");
    }
}