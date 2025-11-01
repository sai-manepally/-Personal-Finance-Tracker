package com.finance.tracker.service;

import com.finance.tracker.model.User;
import com.finance.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    public User createUser(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        if (!passwordService.isPasswordStrong(password)) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }

        // Hash the password before saving
        String hashedPassword = passwordService.hashPassword(password);
        User user = new User(name, email, hashedPassword);
        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Verify password
            if (passwordService.verifyPassword(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, String name, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Verify old password
            if (passwordService.verifyPassword(oldPassword, user.getPassword())) {
                if (!passwordService.isPasswordStrong(newPassword)) {
                    throw new RuntimeException("New password must be at least 6 characters long");
                }
                // Hash and save new password
                user.setPassword(passwordService.hashPassword(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}