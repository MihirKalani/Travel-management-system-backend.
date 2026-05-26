package com.travelManagement.tms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.travelManagement.tms.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// This is the main User table.
// Every person in the system (admin, employee, manager, finance) is stored here.
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    // Unique code for each user like EMP001, MGR002, FIN003
    // Always saved in UPPERCASE — combination of alphabets and numbers
    @Column(name = "user_code", unique = true)
    private String userCode;

    // Phone number of the user
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // What role does this user have in the system
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.employee;

    // Which department does the user belong to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Department department;

    // Who is this user's manager
    // If the manager is deleted, this field becomes NULL automatically
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @JsonIgnoreProperties({"manager", "department", "passwordHash", "hibernateLazyInitializer", "handler"})
    private User manager;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Before saving to database, make sure userCode is always UPPERCASE
    @PrePersist
    public void onBeforeCreate() {
        if (this.userCode != null) {
            this.userCode = this.userCode.toUpperCase();
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.userCode != null) {
            this.userCode = this.userCode.toUpperCase();
        }
    }
}
