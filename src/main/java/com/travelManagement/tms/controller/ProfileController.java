package com.travelManagement.tms.controller;

import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// This controller handles profile-related actions.
// Users can view their profile and change their password here.
@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    // Get the profile info for a specific user
    @GetMapping("/{userId}")
    public ResponseEntity<User> getProfile(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return ResponseEntity.ok(user);
    }

    // Change password — user sends old password and new password
    @PutMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> passwords) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");

        // Check if the old password matches what's in the database
        if (!user.getPasswordHash().equals(oldPassword)) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Make sure new password is not empty
        if (newPassword == null || newPassword.isBlank()) {
            throw new RuntimeException("New password cannot be empty");
        }

        // Save the new password
        user.setPasswordHash(newPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully");
    }
}
