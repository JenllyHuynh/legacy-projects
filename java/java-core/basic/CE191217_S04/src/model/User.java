package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * S04 - Write a login function uses MD5 encryption for passwords (separate from FPT Webmail software Project
 * Represents a User entity in the system.
 * Contains user authentication and profile information.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class User {
    // Declare a private String variable to store the user's username
    private String username;
    // Declare a private String variable to store the MD5 hashed password
    private String passwordMD5;
    // Declare a private String variable to store the user's full name
    private String name;
    // Declare a private String variable to store the user's phone number
    private String phone;
    // Declare a private String variable to store the user's email address
    private String email;
    // Declare a private String variable to store the user's physical address
    private String address;
    // Declare a private LocalDate variable to store the user's date of birth
    private LocalDate dob;

    /**
     * Constructs a new User with the specified details.
     *
     * @param username    the user's unique username
     * @param passwordMD5 the user's password in MD5 hash format
     * @param name        the user's full name
     * @param phone       the user's phone number
     * @param email       the user's email address
     * @param address     the user's physical address
     * @param dob         the user's date of birth in "dd/MM/yyyy" format
     */
    public User(String username, String passwordMD5, String name, String phone,
                String email, String address, String dob) {
        // Assign the username parameter value to the instance variable
        this.username = username;
        // Assign the MD5 hashed password parameter value to the instance variable
        this.passwordMD5 = passwordMD5;
        // Assign the name parameter value to the instance variable
        this.name = name;
        // Assign the phone parameter value to the instance variable
        this.phone = phone;
        // Assign the email parameter value to the instance variable
        this.email = email;
        // Assign the address parameter value to the instance variable
        this.address = address;
        // Parse the dob string using "dd/MM/yyyy" format and assign to LocalDate instance variable
        this.dob = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Retrieves the user's username.
     *
     * @return the username
     */
    public String getUsername() {
        // Return the username value
        return username;
    }

    /**
     * Retrieves the user's password in MD5 hashed format.
     *
     * @return the MD5 hashed password
     */
    public String getPasswordMD5() {
        // Return the MD5 hashed password value
        return passwordMD5;
    }

    /**
     * Retrieves the user's full name.
     *
     * @return the full name
     */
    public String getName() {
        // Return the name value
        return name;
    }

    /**
     * Retrieves the user's contact phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        // Return the phone value
        return phone;
    }

    /**
     * Retrieves the user's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        // Return the email value
        return email;
    }

    /**
     * Retrieves the user's residential or physical address.
     *
     * @return the physical address
     */
    public String getAddress() {
        // Return the address value
        return address;
    }

    /**
     * Retrieves the user's date of birth.
     *
     * @return the date of birth as a LocalDate object
     */
    public LocalDate getDob() {
        // Return the dob LocalDate value
        return dob;
    }


    /**
     * Updates the user's password with a new MD5 hash.
     * Used when changing passwords.
     *
     * @param newPasswordMD5 the new password in MD5 hash format
     */
    public void setPasswordMD5(String newPasswordMD5) {
        // Assign the new MD5 hashed password to the instance variable
        this.passwordMD5 = newPasswordMD5;
    }

    /**
     * Returns a formatted string representation of the user for display.
     * Password is masked for security.
     *
     * @return formatted user information with masked password
     */
    @Override
    public String toString() {
        // Return a formatted string containing all user information with labels and newlines
        // Password is masked as "******" for security purposes
        return String.format("Account:%s\nPassword:%s\nName:%s\nPhone:%s\nEmail:%s\nAddress:%s\nDOB:%s",
                username, "******", name, phone, email, address,
                dob.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}