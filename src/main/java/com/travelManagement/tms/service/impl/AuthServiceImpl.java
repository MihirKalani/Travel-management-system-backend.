package com.travelManagement.tms.service.impl;

import com.travelManagement.tms.dto.DepartmentDTO;
import com.travelManagement.tms.dto.LoginRequest;
import com.travelManagement.tms.dto.UserResponseDTO;
import com.travelManagement.tms.entity.Department;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.exception.ResourceNotFoundException;
import com.travelManagement.tms.repository.UserRepository;
import com.travelManagement.tms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

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

    /**
     * Simple plaintext credential check.
     * Returns the authenticated User DTO on success.
     */
    @Override
    public UserResponseDTO authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No user found with email: " + loginRequest.getEmail()));

        if (!loginRequest.getPassword().equals(user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return toUserResponseDTO(user);
    }
}
