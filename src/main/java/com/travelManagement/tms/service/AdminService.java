package com.travelManagement.tms.service;

import com.travelManagement.tms.dto.DepartmentDTO;
import com.travelManagement.tms.dto.UserCreateDTO;
import com.travelManagement.tms.dto.UserResponseDTO;

import java.util.List;

// Service interface for admin operations — user CRUD and department lookups
public interface AdminService {
    UserResponseDTO addUser(UserCreateDTO dto);
    UserResponseDTO updateUser(Long id, UserCreateDTO dto);
    void deleteUser(Long id);
    List<UserResponseDTO> getAllUsers();
    List<UserResponseDTO> getUsersByRole(String role);
    List<UserResponseDTO> getManagers();
    List<DepartmentDTO> getAllDepartments();
}
