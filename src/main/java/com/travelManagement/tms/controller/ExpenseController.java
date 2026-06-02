package com.travelManagement.tms.controller;

import com.travelManagement.tms.dto.ExpenseClaimResponseDTO;
import com.travelManagement.tms.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

// This controller handles expense claims — submitting, approving, rejecting.
// Employees submit expenses with a PDF bill, and finance approves or rejects them.
@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // Employee submits an expense claim with a PDF bill file
    @PostMapping("/claim")
    public ResponseEntity<ExpenseClaimResponseDTO> submitClaim(
            @RequestParam("travelRequestId") Long travelRequestId,
            @RequestParam("userId") Long userId,
            @RequestParam("totalClaimed") BigDecimal totalClaimed,
            @RequestParam(value = "billFile", required = false) MultipartFile billFile) {
        return ResponseEntity.ok(expenseService.submitClaim(travelRequestId, userId, totalClaimed, billFile));
    }

    // Finance approves an expense claim
    @PostMapping("/{claimId}/approve")
    public ResponseEntity<ExpenseClaimResponseDTO> approveClaim(
            @PathVariable Long claimId,
            @RequestParam("approverId") Long approverId,
            @RequestParam(value = "approvedAmount", required = false) BigDecimal approvedAmount) {
        return ResponseEntity.ok(expenseService.approveClaim(claimId, approverId, approvedAmount));
    }

    // Finance rejects an expense claim
    @PostMapping("/{claimId}/reject")
    public ResponseEntity<ExpenseClaimResponseDTO> rejectClaim(
            @PathVariable Long claimId,
            @RequestParam("approverId") Long approverId) {
        return ResponseEntity.ok(expenseService.rejectClaim(claimId, approverId));
    }

    // Finance gets all pending expense claims
    @GetMapping("/pending")
    public ResponseEntity<List<ExpenseClaimResponseDTO>> getPendingClaims() {
        return ResponseEntity.ok(expenseService.getPendingClaims());
    }

    // Employee gets all their expense claims
    @GetMapping("/my/{userId}")
    public ResponseEntity<List<ExpenseClaimResponseDTO>> getMyClaims(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getMyClaims(userId));
    }

    // Download the PDF bill for a specific claim
    @GetMapping("/{claimId}/bill")
    public ResponseEntity<byte[]> downloadBill(@PathVariable Long claimId) {
        String fileName = expenseService.getBillFileName(claimId);
        byte[] pdfData = expenseService.getBillPdf(claimId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .header("Content-Type", "application/pdf")
                .body(pdfData);
    }
}
