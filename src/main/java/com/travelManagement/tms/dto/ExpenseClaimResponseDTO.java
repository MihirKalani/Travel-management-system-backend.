package com.travelManagement.tms.dto;

import com.travelManagement.tms.entity.enums.ReimbursementStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Response DTO for ExpenseClaim entity.
// billPdf binary data is excluded (matches @JsonIgnore on the entity).
@Data
public class ExpenseClaimResponseDTO {
    private Long id;
    private TravelRequestResponseDTO travelRequest;
    private UserResponseDTO.ManagerDTO submittedBy;
    private ReimbursementStatus reimbursementStatus;
    private BigDecimal totalClaimed;
    private BigDecimal totalApproved;
    private String billFileName;
    private LocalDateTime submittedAt;
    private LocalDateTime paidAt;
    private String paymentReference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
