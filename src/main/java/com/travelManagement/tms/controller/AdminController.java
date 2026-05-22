package com.travelManagement.tms.controller;

import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.UserRole;
import com.travelManagement.tms.repository.DepartmentRepository;
import com.travelManagement.tms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /** Create a new user (employee / manager / finance). */
    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        // Resolve department if provided by ID only
        if (user.getDepartment() != null && user.getDepartment().getId() != null) {
            departmentRepository.findById(user.getDepartment().getId())
                    .ifPresent(user::setDepartment);
        }
        // Default role to employee if not set
        if (user.getRole() == null) {
            user.setRole(UserRole.employee);
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    /** Update an existing user. */
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User incoming) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        if (incoming.getFullName() != null)     existing.setFullName(incoming.getFullName());
        if (incoming.getRole() != null)         existing.setRole(incoming.getRole());
        if (incoming.getIsActive() != null)     existing.setIsActive(incoming.getIsActive());
        if (incoming.getPasswordHash() != null && !incoming.getPasswordHash().isBlank())
            existing.setPasswordHash(incoming.getPasswordHash());

        if (incoming.getDepartment() != null && incoming.getDepartment().getId() != null) {
            departmentRepository.findById(incoming.getDepartment().getId())
                    .ifPresent(existing::setDepartment);
        }
        if (incoming.getManager() != null && incoming.getManager().getId() != null) {
            userRepository.findById(incoming.getManager().getId())
                    .ifPresent(existing::setManager);
        }

        return ResponseEntity.ok(userRepository.save(existing));
    }

    /** Get all users. */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /** Get all departments. */
    @GetMapping("/departments")
    public ResponseEntity<?> getAllDepartments() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }
}