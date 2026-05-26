package com.travelManagement.tms.controller;

import com.travelManagement.tms.dto.ApprovalDTO;
import com.travelManagement.tms.entity.TravelRequest;
import com.travelManagement.tms.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// This controller handles all approval and disapproval actions
// for travel requests by both managers and finance.
@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "http://localhost:4200")
public class ApprovalController {

    @Autowired
    private WorkflowService workflowService;

    // Manager approves a request — moves it to finance_pending
    @PostMapping("/{requestId}/manager-approve")
    public ResponseEntity<TravelRequest> managerApprove(
            @PathVariable Long requestId,
            @RequestBody ApprovalDTO dto) {
        return ResponseEntity.ok(workflowService.approveByManager(requestId, dto.getApproverId()));
    }

    // Manager disapproves a request — employee will need to submit a new one
    @PostMapping("/{requestId}/manager-disapprove")
    public ResponseEntity<TravelRequest> managerDisapprove(
            @PathVariable Long requestId,
            @RequestBody ApprovalDTO dto) {
        return ResponseEntity.ok(workflowService.disapproveByManager(requestId, dto.getApproverId(), dto.getComment()));
    }

    // Finance approves a request — travel is fully approved!
    @PostMapping("/{requestId}/finance-approve")
    public ResponseEntity<TravelRequest> financeApprove(
            @PathVariable Long requestId,
            @RequestBody ApprovalDTO dto) {
        return ResponseEntity.ok(workflowService.approveByFinance(requestId, dto.getApproverId()));
    }

    // Finance disapproves a request — employee will need to submit a new one
    @PostMapping("/{requestId}/finance-disapprove")
    public ResponseEntity<TravelRequest> financeDisapprove(
            @PathVariable Long requestId,
            @RequestBody ApprovalDTO dto) {
        return ResponseEntity.ok(workflowService.disapproveByFinance(requestId, dto.getApproverId(), dto.getComment()));
    }

    // Old reject endpoint — kept for backward compatibility
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<TravelRequest> reject(
            @PathVariable Long requestId,
            @RequestBody ApprovalDTO dto) {
        return ResponseEntity.ok(workflowService.reject(requestId, dto.getApproverId()));
    }
}