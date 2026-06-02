package com.travelManagement.tms.service;

import com.travelManagement.tms.dto.LoginRequest;
import com.travelManagement.tms.dto.UserResponseDTO;

// Service interface for authentication
public interface AuthService {
    UserResponseDTO authenticate(LoginRequest loginRequest);
}
