package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * S04 - Write a login function uses MD5 encryption for passwords (separate from FPT Webmail software Project
 * Utility class for MD5 hash generation.
 * Provides method to create MD5 hash from input strings.
 * Author: Huynh Kim Hue CE191217 28/11/2025
 */
public class MD5Utils {
    /**
     * Generates MD5 hash from input string.
     * Converts input string to MD5 hexadecimal representation.
     *
     * @param input the string to hash
     * @return MD5 hash as hexadecimal string (32 characters)
     * @throws RuntimeException if MD5 algorithm is not supported
     */
    public static String hash(String input) {
        try {
            // Get MessageDigest instance for MD5 algorithm
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Generate MD5 hash bytes from input string
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                // Format each byte as two-digit hexadecimal
                sb.append(String.format("%02x", b));
            }

            // Return complete MD5 hash string
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Throw runtime exception if MD5 algorithm is not available
            throw new RuntimeException("MD5 not supported");
        }
    }
}