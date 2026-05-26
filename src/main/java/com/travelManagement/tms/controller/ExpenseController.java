package com.travelManagement.tms.controller;

import com.travelManagement.tms.entity.ExpenseClaim;
import com.travelManagement.tms.entity.Notification;
import com.travelManagement.tms.entity.TravelRequest;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.ReimbursementStatus;
import com.travelManagement.tms.repository.ExpenseClaimRepository;
import com.travelManagement.tms.repository.NotificationRepository;
import com.travelManagement.tms.repository.TravelRequestRepository;
import com.travelManagement.tms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// This controller handles expense claims — submitting, approving, rejecting.
// Employees submit expenses with a PDF bill, and finance approves or rejects them.
@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseController {

    @Autowired
    private ExpenseClaimRepository claimRepository;

    @Autowired
    private TravelRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Employee submits an expense claim with a PDF bill file
    @PostMapping("/claim")
    public ResponseEntity<ExpenseClaim> submitClaim(
            @RequestParam("travelRequestId") Long travelRequestId,
            @RequestParam("userId") Long userId,
            @RequestParam("totalClaimed") BigDecimal totalClaimed,
            @RequestParam(value = "billFile", required = false) MultipartFile billFile) {

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

        return ResponseEntity.ok(claimRepository.save(claim));
    }

    // Finance approves an expense claim
    @PostMapping("/{claimId}/approve")
    public ResponseEntity<ExpenseClaim> approveClaim(
            @PathVariable Long claimId,
            @RequestParam("approverId") Long approverId,
            @RequestParam(value = "approvedAmount", required = false) BigDecimal approvedAmount) {

        ExpenseClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        claim.setReimbursementStatus(ReimbursementStatus.approved);
        claim.setTotalApproved(approvedAmount != null ? approvedAmount : claim.getTotalClaimed());

        claimRepository.save(claim);

        // Notify the employee that their reimbursement was approved!
        Notification notification = new Notification();
        notification.setUser(claim.getSubmittedBy());
        notification.setTitle("Reimbursement Approved!");
        notification.setBody("Your expense claim of ₹" + claim.getTotalApproved() + " has been approved. Reimbursement is on the way!");
        notification.setRelatedEntity("expense_claims");
        notification.setRelatedId(claimId);
        notificationRepository.save(notification);

        return ResponseEntity.ok(claim);
    }

    // Finance rejects an expense claim
    @PostMapping("/{claimId}/reject")
    public ResponseEntity<ExpenseClaim> rejectClaim(
            @PathVariable Long claimId,
            @RequestParam("approverId") Long approverId) {

        ExpenseClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        claim.setReimbursementStatus(ReimbursementStatus.rejected);
        claimRepository.save(claim);

        // Notify the employee about the rejection
        Notification notification = new Notification();
        notification.setUser(claim.getSubmittedBy());
        notification.setTitle("Reimbursement Rejected");
        notification.setBody("Your expense claim was rejected by finance. Please contact your finance team for details.");
        notification.setRelatedEntity("expense_claims");
        notification.setRelatedId(claimId);
        notificationRepository.save(notification);

        return ResponseEntity.ok(claim);
    }

    // Finance gets all pending expense claims
    @GetMapping("/pending")
    public ResponseEntity<List<ExpenseClaim>> getPendingClaims() {
        return ResponseEntity.ok(claimRepository.findByReimbursementStatus(ReimbursementStatus.submitted));
    }

    // Employee gets all their expense claims
    @GetMapping("/my/{userId}")
    public ResponseEntity<List<ExpenseClaim>> getMyClaims(@PathVariable Long userId) {
        return ResponseEntity.ok(claimRepository.findBySubmittedById(userId));
    }

    // Download the PDF bill for a specific claim
    @GetMapping("/{claimId}/bill")
    public ResponseEntity<byte[]> downloadBill(@PathVariable Long claimId) {
        ExpenseClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        if (claim.getBillPdf() == null) {
            throw new RuntimeException("No bill uploaded for this claim");
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + claim.getBillFileName() + "\"")
                .header("Content-Type", "application/pdf")
                .body(claim.getBillPdf());
    }
}
