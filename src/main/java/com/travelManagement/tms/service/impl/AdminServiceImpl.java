package com.travelManagement.tms.service.impl;

import com.travelManagement.tms.dto.DepartmentDTO;
import com.travelManagement.tms.dto.UserCreateDTO;
import com.travelManagement.tms.dto.UserResponseDTO;
import com.travelManagement.tms.entity.Department;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.UserRole;
import com.travelManagement.tms.repository.DepartmentRepository;
import com.travelManagement.tms.repository.UserRepository;
import com.travelManagement.tms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

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

    private List<DepartmentDTO> toDepartmentDTOList(List<Department> list) {
        return list.stream().map(this::toDepartmentDTO).collect(Collectors.toList());
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

    private List<UserResponseDTO> toUserResponseDTOList(List<User> list) {
        return list.stream().map(this::toUserResponseDTO).collect(Collectors.toList());
    }

    // ──── Service Methods ────

    // Post mapping for creating user (employee, manager, finance, admin)
    @Override
    public UserResponseDTO addUser(UserCreateDTO dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPasswordHash(dto.getPasswordHash());

        // Default role to employee if not provided
        user.setRole(dto.getRole() != null ? dto.getRole() : UserRole.employee);

        // save user code in uppercase
        if (dto.getUserCode() != null) {
            user.setUserCode(dto.getUserCode().toUpperCase());
        }

        // If a department ID was provided, look up the department and assign it
        if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
            departmentRepository.findById(dto.getDepartment().getId())
                    .ifPresent(user::setDepartment);
        }

        // If the new user is an employee and a manager was assigned, it finds that manager in the database and links them together.
        if (dto.getManager() != null && dto.getManager().getId() != null) {
            userRepository.findById(dto.getManager().getId())
                    .ifPresent(user::setManager);
        }

        User saved = userRepository.save(user);
        return toUserResponseDTO(saved);
    }

    // Update an existing user
    @Override
    public UserResponseDTO updateUser(Long id, UserCreateDTO dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        // Only update fields that were actually sent (not null)
        if (dto.getFullName() != null)     existing.setFullName(dto.getFullName());
        if (dto.getRole() != null)         existing.setRole(dto.getRole());
        if (dto.getIsActive() != null)     existing.setIsActive(dto.getIsActive());
        if (dto.getUserCode() != null)     existing.setUserCode(dto.getUserCode().toUpperCase());
        if (dto.getPhoneNumber() != null)  existing.setPhoneNumber(dto.getPhoneNumber());

        // Update password only if a new one was provided
        if (dto.getPasswordHash() != null && !dto.getPasswordHash().isBlank()) {
            existing.setPasswordHash(dto.getPasswordHash());
        }

        // Update department if provided
        if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
            departmentRepository.findById(dto.getDepartment().getId())
                    .ifPresent(existing::setDepartment);
        }

        // Update manager if provided
        if (dto.getManager() != null && dto.getManager().getId() != null) {
            userRepository.findById(dto.getManager().getId())
                    .ifPresent(existing::setManager);
        }

        User saved = userRepository.save(existing);
        return toUserResponseDTO(saved);
    }

    // Delete a user by ID
    @Override
    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete user. They have associated travel requests or expenses. Please mark them as Inactive instead.");
        }
    }

    // Get all users in the system
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return toUserResponseDTOList(userRepository.findAll());
    }

    // Get users filtered by role (employee, manager, finance, admin)
    // Used to show separate tables for each role in the admin panel
    @Override
    public List<UserResponseDTO> getUsersByRole(String role) {
        UserRole userRole = UserRole.valueOf(role.toLowerCase());
        return toUserResponseDTOList(userRepository.findByRole(userRole));
    }

    // create manager dropdown in the frontend, this endpoint will return all users with the manager role
    @Override
    public List<UserResponseDTO> getManagers() {
        return toUserResponseDTOList(userRepository.findByRole(UserRole.manager));
    }

    // create department dropdown in the frontend, this endpoint will return all departments in the system
    @Override
    public List<DepartmentDTO> getAllDepartments() {
        return toDepartmentDTOList(departmentRepository.findAll());
    }
}
