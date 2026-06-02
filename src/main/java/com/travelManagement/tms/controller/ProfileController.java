package com.travelManagement.tms.controller;

import com.travelManagement.tms.dto.ChangePasswordDTO;
import com.travelManagement.tms.dto.UserResponseDTO;
import com.travelManagement.tms.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// This controller handles profile-related actions.
// Users can view their profile and change their password here.
@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // Get the profile info for a specific user
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    // Change password — user sends old password and new password
    @PutMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordDTO dto) {
        return ResponseEntity.ok(profileService.changePassword(userId, dto));
    }
}
