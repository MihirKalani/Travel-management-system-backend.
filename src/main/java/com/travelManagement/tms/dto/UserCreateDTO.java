package com.travelManagement.tms.dto;

import com.travelManagement.tms.entity.enums.UserRole;
import lombok.Data;

// Request DTO for creating and updating users.
// Matches the JSON payload the frontend sends (nested department/manager objects with just id).
@Data
public class UserCreateDTO {
    private String fullName;
    private String email;
    private String userCode;
    private String phoneNumber;
    private String passwordHash;
    private UserRole role;
    private Boolean isActive;

    // Frontend sends department as { id: 123 }
    private IdHolder department;

    // Frontend sends manager as { id: 456 }
    private IdHolder manager;

    @Data
    public static class IdHolder {
        private Long id;
    }
}
