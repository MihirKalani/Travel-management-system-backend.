package com.travelManagement.tms.dto;

import lombok.Data;

@Data
public class ApprovalDTO {
    private Long approverId;
    private String comment;
    private java.math.BigDecimal budgetOverride;
}