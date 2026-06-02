package com.travelManagement.tms.dto;

import com.travelManagement.tms.entity.enums.UserRole;
import lombok.Data;
import java.time.LocalDateTime;

// Response DTO for User entity.
// Matches the JSON shape the frontend already expects from the User entity.
// passwordHash is never exposed. Manager is a compact nested object to avoid circular refs.
@Data
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String userCode;
    private String phoneNumber;
    private UserRole role;
    private DepartmentDTO department;
    private ManagerDTO manager;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Compact manager representation — matches what @JsonIgnoreProperties produced on the entity
    @Data
    public static class ManagerDTO {
        private Long id;
        private String fullName;
        private String email;
        private String userCode;
        private String phoneNumber;
        private UserRole role;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
