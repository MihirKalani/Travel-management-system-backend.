package com.travelManagement.tms.controller;

import com.travelManagement.tms.dto.ApprovalDTO;
import com.travelManagement.tms.entity.TravelRequest;
import com.travelManagement.tms.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "http://localhost:4200")
public class ApprovalController {

    @Autowired
    private WorkflowService workflowService;

    /** Manager approves a pending_manager request → moves to pending_finance */
    @PostMapping("/{requestId}/manager-approve")
    public ResponseEntity<TravelRequest> managerApprove(
            @PathVariable Long requestId,
            @RequestBody ApprovalDTO dto) {
        return ResponseEntity.ok(workflowService.approveByManager(requestId, dto.getApproverId()));
    }

    /** Finance approves a pending_finance request → moves to approved */
    @PostMapping("/{requestId}/finance-approve")
    public ResponseEntity<TravelRequest> financeApprove(
            @PathVariable Long requestId,
            @RequestBody ApprovalDTO dto) {
        return ResponseEntity.ok(workflowService.approveByFinance(requestId, dto.getApproverId()));
    }

    /** Reject a request (manager or finance) */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<TravelRequest> reject(
            @PathVariable Long requestId,
            @RequestBody ApprovalDTO dto) {
        return ResponseEntity.ok(workflowService.reject(requestId, dto.getApproverId()));
    }
}