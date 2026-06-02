package com.travelManagement.tms.service.impl;

import com.travelManagement.tms.dto.ChangePasswordDTO;
import com.travelManagement.tms.dto.DepartmentDTO;
import com.travelManagement.tms.dto.UserResponseDTO;
import com.travelManagement.tms.entity.Department;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.repository.UserRepository;
import com.travelManagement.tms.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// This service handles profile-related actions.
// Users can view their profile and change their password here.
@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    // ──── DTO Mapping Methods (private to this service) ────

    private DepartmentDTO toDepartmentDTO(Department d) {
        if (d == null) return null;
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(d.getId());
        dto.setName(d.getName());
        dto.setCode(d.getCode());
        dto.setCreatedAt(d.getCreatedAt());
        dto.setUpdatedAt(d.getUpdatedAt());
        return dto;
    }

    private UserResponseDTO.ManagerDTO toCompactUserDTO(User u) {
        if (u == null) return null;
        UserResponseDTO.ManagerDTO dto = new UserResponseDTO.ManagerDTO();
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setUserCode(u.getUserCode());
        dto.setPhoneNumber(u.getPhoneNumber());
        dto.setRole(u.getRole());
        dto.setIsActive(u.getIsActive());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    }

    private UserResponseDTO toUserResponseDTO(User u) {
        if (u == null) return null;
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setUserCode(u.getUserCode());
        dto.setPhoneNumber(u.getPhoneNumber());
        dto.setRole(u.getRole());
        dto.setDepartment(toDepartmentDTO(u.getDepartment()));
        dto.setManager(toCompactUserDTO(u.getManager()));
        dto.setIsActive(u.getIsActive());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    }

    // ──── Service Methods ────

    // Get the profile info for a specific user
    @Override
    public UserResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return toUserResponseDTO(user);
    }

    // Change password — user sends old password and new password
    @Override
    public String changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Check if the old password matches what's in the database
        if (!user.getPasswordHash().equals(dto.getOldPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Make sure new password is not empty
        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new RuntimeException("New password cannot be empty");
        }

        // Save the new password
        user.setPasswordHash(dto.getNewPassword());
        userRepository.save(user);

        return "Password changed successfully";
    }
}
