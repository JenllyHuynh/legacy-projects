package service;

import model.User;
import util.MD5Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * S04 - Write a login function uses MD5 encryption for passwords (separate from FPT Webmail software Project
 * Service class for handling user authentication and account management.
 * Manages user registration, login, password changes, and user data.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class AuthService {
    // Declare a private final List to store all registered users
    private final List<User> users = new ArrayList<>();

    /**
     * Adds a new user account to the system.
     * Validates input parameters and checks for duplicate usernames.
     *
     * @param username the unique username for the new account
     * @param password the plain text password (will be hashed)
     * @param name     the user's full name
     * @param phone    the user's phone number
     * @param email    the user's email address
     * @param address  the user's physical address
     * @param dob      the user's date of birth in "dd/MM/yyyy" format
     * @return the total number of users after adding (1-indexed)
     * @throws Exception if validation fails or username already exists
     */
    public int addAccount(String username, String password, String name, String phone,
                          String email, String address, String dob) throws Exception {

        // ---------- VALIDATION SECTION ----------
        // Validate username is not null or empty
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username cannot be empty!");
        }

        // Validate username does not contain '#'
        if (username.contains("#")) {
            throw new Exception("Username cannot contain '#' character!");
        }

        // Validate password is not null or empty
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Password cannot be empty!");
        }

        // Validate password does not contain '#'
        if (password.contains("#")) {
            throw new Exception("Password cannot contain '#' character!");
        }

        // Validate name does not contain '#'
        if (name.contains("#")) {
            throw new Exception("Name cannot contain '#' character!");
        }

        // Validate phone does not contain '#'
        if (phone.contains("#")) {
            throw new Exception("Phone cannot contain '#' character!");
        }

        // Validate email does not contain '#'
        if (email.contains("#")) {
            throw new Exception("Email cannot contain '#' character!");
        }

        // Validate date of birth does not contain '#'
        if (dob.contains("#")) {
            throw new Exception("Date of birth cannot contain '#' character!");
        }

        // Check if username already exists in the system
        if (findByUsername(username) != null) {
            throw new Exception("Username already exists!");
        }

        // ---------- ACCOUNT CREATION SECTION ----------
        // Hash the plain text password using MD5
        String md5Pass = MD5Utils.hash(password);
        // Create new User object with hashed password
        User user = new User(username, md5Pass, name, phone, email, address, dob);
        // Add user to the users list
        users.add(user);
        // Return the new total number of users (1-indexed)
        return users.size();
    }

    /**
     * Authenticates a user with username and password.
     * Compares MD5 hashed password with stored hash.
     *
     * @param username the username to authenticate
     * @param password the plain text password to verify
     * @return true if authentication succeeds, false otherwise
     */
    public boolean login(String username, String password) {
        // Hash the input password using MD5
        String md5Input = MD5Utils.hash(password);
        // Iterate through all users to find matching credentials
        for (User u : users) {
            // Check if username and hashed password match
            if (u.getUsername().equals(username) && u.getPasswordMD5().equals(md5Input)) {
                // Authentication successful
                return true;
            }
        }
        // Authentication failed
        return false;
    }

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return the User object if found, null otherwise
     */
    public User findByUsername(String username) {
        // Iterate through all users
        for (User u : users) {
            // Check if username matches
            if (u.getUsername().equals(username)) return u;
        }
        // User not found
        return null;
    }

    /**
     * Changes the password for a specific user.
     * Hashes the new password before storing.
     *
     * @param username the username whose password should be changed
     * @param newPass  the new plain text password
     */
    public void changePassword(String username, String newPass) {
        // Find user by username
        User u = findByUsername(username);
        // If user exists, update password with MD5 hash
        if (u != null) {
            u.setPasswordMD5(MD5Utils.hash(newPass));
        }
    }

    /**
     * Returns a copy of all registered users.
     * Uses defensive copying to prevent external modification.
     *
     * @return a new ArrayList containing all users
     */
    public List<User> getAllUsers() {
        // Return defensive copy of users list
        return new ArrayList<>(users);
    }
}