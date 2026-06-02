package com.travelManagement.tms.service;

import com.travelManagement.tms.dto.ChangePasswordDTO;
import com.travelManagement.tms.dto.UserResponseDTO;

// Service interface for user profile operations
public interface ProfileService {
    UserResponseDTO getProfile(Long userId);
    String changePassword(Long userId, ChangePasswordDTO dto);
}
