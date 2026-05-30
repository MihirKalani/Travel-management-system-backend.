package com.travelManagement.tms.controller;

import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.UserRole;
import com.travelManagement.tms.repository.DepartmentRepository;
import com.travelManagement.tms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// i create this controller to handle fronted requests related to admin functions

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // Post mapping for creating user (employee, manager, finance, admin)
    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user) {

        // If a department ID was provided, look up the department and assign it
        if (user.getDepartment() != null && user.getDepartment().getId() != null) {
            departmentRepository.findById(user.getDepartment().getId())
                    .ifPresent(user::setDepartment);
        }

        // Default role to employee if not provided
        if (user.getRole() == null) {
            user.setRole(UserRole.employee);
        }

        // save user code in uppercase
        if (user.getUserCode() != null) {
            user.setUserCode(user.getUserCode().toUpperCase());
        }

        // If a manager ID was provided, look up the manager and assign them
        if (user.getManager() != null && user.getManager().getId() != null) {
            userRepository.findById(user.getManager().getId())
                    .ifPresent(user::setManager);
        }

        return ResponseEntity.ok(userRepository.save(user));
    }

    // Update an existing user
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User incoming) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        // Only update fields that were actually sent (not null)
        if (incoming.getFullName() != null)     existing.setFullName(incoming.getFullName());
        if (incoming.getRole() != null)         existing.setRole(incoming.getRole());
        if (incoming.getIsActive() != null)     existing.setIsActive(incoming.getIsActive());
        if (incoming.getUserCode() != null)     existing.setUserCode(incoming.getUserCode().toUpperCase());
        if (incoming.getPhoneNumber() != null)  existing.setPhoneNumber(incoming.getPhoneNumber());

        // Update password only if a new one was provided
        if (incoming.getPasswordHash() != null && !incoming.getPasswordHash().isBlank()) {
            existing.setPasswordHash(incoming.getPasswordHash());
        }

        // Update department if provided
        if (incoming.getDepartment() != null && incoming.getDepartment().getId() != null) {
            departmentRepository.findById(incoming.getDepartment().getId())
                    .ifPresent(existing::setDepartment);
        }

        // Update manager if provided
        if (incoming.getManager() != null && incoming.getManager().getId() != null) {
            userRepository.findById(incoming.getManager().getId())
                    .ifPresent(existing::setManager);
        }

        return ResponseEntity.ok(userRepository.save(existing));
    }

    // now create a get user with a filter

    // Get all users in the system
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Get users filtered by role (employee, manager, finance, admin)
    // Used to show separate tables for each role in the admin panel
    @GetMapping("/users/by-role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        UserRole userRole = UserRole.valueOf(role.toLowerCase());
        return ResponseEntity.ok(userRepository.findByRole(userRole));
    }

    // create manager dropdown in the frontend, this endpoint will return all users with the manager role
    @GetMapping("/users/managers")
    public ResponseEntity<List<User>> getManagers() {
        return ResponseEntity.ok(userRepository.findByRole(UserRole.manager));
    }

    // create department dropdown in the frontend, this endpoint will return all departments in the system
    @GetMapping("/departments")
    public ResponseEntity<?> getAllDepartments() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }
}