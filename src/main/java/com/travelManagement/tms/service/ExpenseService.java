package com.travelManagement.tms.service;

import com.travelManagement.tms.dto.ExpenseClaimResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

// Service interface for expense claim operations
public interface ExpenseService {
    ExpenseClaimResponseDTO submitClaim(Long travelRequestId, Long userId, BigDecimal totalClaimed, MultipartFile billFile);
    ExpenseClaimResponseDTO approveClaim(Long claimId, Long approverId, BigDecimal approvedAmount);
    ExpenseClaimResponseDTO rejectClaim(Long claimId, Long approverId);
    List<ExpenseClaimResponseDTO> getPendingClaims();
    List<ExpenseClaimResponseDTO> getMyClaims(Long userId);
    byte[] getBillPdf(Long claimId);
    String getBillFileName(Long claimId);
}
