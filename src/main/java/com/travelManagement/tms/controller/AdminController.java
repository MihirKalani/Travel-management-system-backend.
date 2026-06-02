package com.travelManagement.tms.controller;

import com.travelManagement.tms.dto.DepartmentDTO;
import com.travelManagement.tms.dto.UserCreateDTO;
import com.travelManagement.tms.dto.UserResponseDTO;
import com.travelManagement.tms.service.AdminService;
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
    private AdminService adminService;

    // Post mapping for creating user (employee, manager, finance, admin)
    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> addUser(@RequestBody UserCreateDTO dto) {
        return ResponseEntity.ok(adminService.addUser(dto));
    }

    // Update an existing user
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserCreateDTO dto) {
        return ResponseEntity.ok(adminService.updateUser(id, dto));
    }

    // Delete a user by ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // now create a get user with a filter
    // Get all users in the system
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // Get users filtered by role (employee, manager, finance, admin)
    // Used to show separate tables for each role in the admin panel
    @GetMapping("/users/by-role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(adminService.getUsersByRole(role));
    }

    // create manager dropdown in the frontend, this endpoint will return all users with the manager role
    @GetMapping("/users/managers")
    public ResponseEntity<List<UserResponseDTO>> getManagers() {
        return ResponseEntity.ok(adminService.getManagers());
    }

    // create department dropdown in the frontend, this endpoint will return all departments in the system
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        return ResponseEntity.ok(adminService.getAllDepartments());
    }

}
