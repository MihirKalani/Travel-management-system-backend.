package com.travelManagement.tms.service.impl;

import com.travelManagement.tms.dto.DepartmentDTO;
import com.travelManagement.tms.dto.ExpenseClaimResponseDTO;
import com.travelManagement.tms.dto.TravelRequestResponseDTO;
import com.travelManagement.tms.dto.UserResponseDTO;
import com.travelManagement.tms.entity.*;
import com.travelManagement.tms.entity.enums.ReimbursementStatus;
import com.travelManagement.tms.repository.ExpenseClaimRepository;
import com.travelManagement.tms.repository.TravelRequestRepository;
import com.travelManagement.tms.repository.UserRepository;
import com.travelManagement.tms.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// This service handles expense claims — submitting, approving, rejecting.
// Employees submit expenses with a PDF bill, and finance approves or rejects them.
@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseClaimRepository claimRepository;

    @Autowired
    private TravelRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    // ──── DTO Mapping Methods (private to this service) ────

    private DepartmentDTO toDepartmentDTO(Department d) {
        if (d == null) return null;
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(d.getId());
        dto.setName(d.getName());
        dto.setCode(d.getCode());
        dto.setCreatedAt(d.getCreatedAt());
        dto.setUpdatedAt(d.getUpdatedAt());
        return dto;
    }

    private UserResponseDTO.ManagerDTO toCompactUserDTO(User u) {
        if (u == null) return null;
        UserResponseDTO.ManagerDTO dto = new UserResponseDTO.ManagerDTO();
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setUserCode(u.getUserCode());
        dto.setPhoneNumber(u.getPhoneNumber());
        dto.setRole(u.getRole());
        dto.setIsActive(u.getIsActive());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    }

    private TravelRequestResponseDTO toTravelRequestResponseDTO(TravelRequest r) {
        if (r == null) return null;
        TravelRequestResponseDTO dto = new TravelRequestResponseDTO();
        dto.setId(r.getId());
        dto.setReferenceNo(r.getReferenceNo());
        dto.setRequester(toCompactUserDTO(r.getRequester()));
        dto.setTripType(r.getTripType());
        dto.setDestination(r.getDestination());
        dto.setStartingPlace(r.getStartingPlace());
        dto.setPurpose(r.getPurpose());
        dto.setTravelFrom(r.getTravelFrom());
        dto.setTravelTo(r.getTravelTo());
        dto.setTransportMode(r.getTransportMode());
        dto.setTravelClass(r.getTravelClass());
        dto.setEstimatedBudget(r.getEstimatedBudget());
        dto.setApprovedBudget(r.getApprovedBudget());
        dto.setStatus(r.getStatus());
        dto.setAssignedManager(toCompactUserDTO(r.getAssignedManager()));
        dto.setOverrideNote(r.getOverrideNote());
        dto.setSubmittedAt(r.getSubmittedAt());
        dto.setApprovedAt(r.getApprovedAt());
        dto.setRejectedAt(r.getRejectedAt());
        dto.setCompletedAt(r.getCompletedAt());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }

    private ExpenseClaimResponseDTO toExpenseClaimResponseDTO(ExpenseClaim c) {
        if (c == null) return null;
        ExpenseClaimResponseDTO dto = new ExpenseClaimResponseDTO();
        dto.setId(c.getId());
        dto.setTravelRequest(toTravelRequestResponseDTO(c.getTravelRequest()));
        dto.setSubmittedBy(toCompactUserDTO(c.getSubmittedBy()));
        dto.setReimbursementStatus(c.getReimbursementStatus());
        dto.setTotalClaimed(c.getTotalClaimed());
        dto.setTotalApproved(c.getTotalApproved());
        dto.setBillFileName(c.getBillFileName());
        dto.setSubmittedAt(c.getSubmittedAt());
        dto.setPaidAt(c.getPaidAt());
        dto.setPaymentReference(c.getPaymentReference());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }

    private List<ExpenseClaimResponseDTO> toExpenseClaimResponseDTOList(List<ExpenseClaim> list) {
        return list.stream().map(this::toExpenseClaimResponseDTO).collect(Collectors.toList());
    }

    // ──── Service Methods ────

    // Employee submits an expense claim with a PDF bill file
    @Override
    public ExpenseClaimResponseDTO submitClaim(Long travelRequestId, Long userId, BigDecimal totalClaimed, MultipartFile billFile) {
        // Find the travel request this claim belongs to
        TravelRequest travelRequest = requestRepository.findById(travelRequestId)
                .orElseThrow(() -> new RuntimeException("Travel request not found: " + travelRequestId));

        // Find the user who is submitting this claim
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Check if a claim already exists for this travel request
        ExpenseClaim claim = claimRepository.findByTravelRequestId(travelRequestId)
                .orElse(new ExpenseClaim());

        claim.setTravelRequest(travelRequest);
        claim.setSubmittedBy(user);
        claim.setTotalClaimed(totalClaimed);
        claim.setReimbursementStatus(ReimbursementStatus.submitted);
        claim.setSubmittedAt(LocalDateTime.now());

        // If a PDF bill was uploaded, save it as binary data in the database
        if (billFile != null && !billFile.isEmpty()) {
            try {
                claim.setBillPdf(billFile.getBytes());
                claim.setBillFileName(billFile.getOriginalFilename());
            } catch (Exception e) {
                throw new RuntimeException("Failed to read uploaded file: " + e.getMessage());
            }
        }

        ExpenseClaim saved = claimRepository.save(claim);
        return toExpenseClaimResponseDTO(saved);
    }

    // Finance approves an expense claim
    @Override
    public ExpenseClaimResponseDTO approveClaim(Long claimId, Long approverId, BigDecimal approvedAmount) {
        ExpenseClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        claim.setReimbursementStatus(ReimbursementStatus.approved);
        claim.setTotalApproved(approvedAmount != null ? approvedAmount : claim.getTotalClaimed());

        ExpenseClaim saved = claimRepository.save(claim);
        return toExpenseClaimResponseDTO(saved);
    }

    // Finance rejects an expense claim
    @Override
    public ExpenseClaimResponseDTO rejectClaim(Long claimId, Long approverId) {
        ExpenseClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        claim.setReimbursementStatus(ReimbursementStatus.rejected);
        ExpenseClaim saved = claimRepository.save(claim);
        return toExpenseClaimResponseDTO(saved);
    }

    // Finance gets all pending expense claims
    @Override
    public List<ExpenseClaimResponseDTO> getPendingClaims() {
        return toExpenseClaimResponseDTOList(
                claimRepository.findByReimbursementStatus(ReimbursementStatus.submitted));
    }

    // Employee gets all their expense claims
    @Override
    public List<ExpenseClaimResponseDTO> getMyClaims(Long userId) {
        return toExpenseClaimResponseDTOList(claimRepository.findBySubmittedById(userId));
    }

    // Get the PDF bill binary data for a specific claim
    @Override
    public byte[] getBillPdf(Long claimId) {
        ExpenseClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        if (claim.getBillPdf() == null) {
            throw new RuntimeException("No bill uploaded for this claim");
        }
        return claim.getBillPdf();
    }

    // Get the file name of the PDF bill for a specific claim
    @Override
    public String getBillFileName(Long claimId) {
        ExpenseClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));
        return claim.getBillFileName();
    }
}
