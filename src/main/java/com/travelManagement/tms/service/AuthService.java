package com.travelManagement.tms.service;

import com.travelManagement.tms.dto.LoginRequest;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.exception.ResourceNotFoundException;
import com.travelManagement.tms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Simple plaintext credential check.
     * Returns the authenticated User on success.
     */
    public User authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No user found with email: " + loginRequest.getEmail()));

        if (!loginRequest.getPassword().equals(user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }
}
