
package com.finance.tracker.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class PasswordService {

    /**
     * Hash a password using SHA-256
     */
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verify if a password matches the hashed password
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(hashedPassword);
    }

    /**
     * Validate password strength (optional)
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // Add more rules if needed (uppercase, numbers, special chars, etc.)
        return true;
    }
}