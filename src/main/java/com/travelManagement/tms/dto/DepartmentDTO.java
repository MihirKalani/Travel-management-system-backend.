package com.travelManagement.tms.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DepartmentDTO {
    private Long id;
    private String name;
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
