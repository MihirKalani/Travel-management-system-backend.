package com.travelManagement.tms.dto;

import lombok.Data;

// Replaces the raw Map<String, String> that was used in ProfileController
@Data
public class ChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
}
