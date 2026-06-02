package com.travelManagement.tms.controller;

import com.travelManagement.tms.dto.TravelRequestDTO;
import com.travelManagement.tms.dto.TravelRequestResponseDTO;
import com.travelManagement.tms.service.TravelRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// This controller handles everything related to travel requests:
// creating drafts, submitting, deleting, and fetching requests.
@RestController
@RequestMapping("/api/travel-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class TravelRequestController {

    @Autowired
    private TravelRequestService travelRequestService;

    // Save a travel request as Draft
    @PostMapping("/draft")
    public ResponseEntity<TravelRequestResponseDTO> saveDraft(@RequestBody TravelRequestDTO dto) {
        return ResponseEntity.ok(travelRequestService.saveDraft(dto));
    }

    // Submit a draft — changes status to manager_pending
    @PostMapping("/{id}/submit")
    public ResponseEntity<TravelRequestResponseDTO> submitRequest(@PathVariable Long id) {
        return ResponseEntity.ok(travelRequestService.submitRequest(id));
    }

    // Delete a draft request (only drafts can be deleted)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraft(@PathVariable Long id) {
        travelRequestService.deleteDraft(id);
        return ResponseEntity.noContent().build();
    }

    // Get all travel requests for a specific user
    @GetMapping("/employee/{userId}")
    public ResponseEntity<List<TravelRequestResponseDTO>> getMyRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(travelRequestService.getMyRequests(userId));
    }

    // Get a single travel request by its ID
    @GetMapping("/{id}")
    public ResponseEntity<TravelRequestResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(travelRequestService.getById(id));
    }

    // Get all requests — used by admin and finance
    @GetMapping
    public ResponseEntity<List<TravelRequestResponseDTO>> getAll() {
        return ResponseEntity.ok(travelRequestService.getAll());
    }

    // Get requests pending manager approval for a specific manager
    @GetMapping("/pending/manager/{managerId}")
    public ResponseEntity<List<TravelRequestResponseDTO>> getPendingForManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(travelRequestService.getPendingForManager(managerId));
    }

    // Get all requests pending finance approval
    @GetMapping("/pending/finance")
    public ResponseEntity<List<TravelRequestResponseDTO>> getPendingForFinance() {
        return ResponseEntity.ok(travelRequestService.getPendingForFinance());
    }
}