package com.travelManagement.tms.controller;

import com.travelManagement.tms.dto.TravelRequestDTO;
import com.travelManagement.tms.entity.TravelRequest;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.TripStatus;
import com.travelManagement.tms.entity.enums.TripType;
import com.travelManagement.tms.entity.enums.UserRole;
import com.travelManagement.tms.repository.TravelRequestRepository;
import com.travelManagement.tms.repository.UserRepository;
import com.travelManagement.tms.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// This controller handles everything related to travel requests:
// creating drafts, submitting, deleting, and fetching requests.
@RestController
@RequestMapping("/api/travel-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class TravelRequestController {

    @Autowired
    private TravelRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkflowService workflowService;

    // Generate a reference number like TRV-2026-00001
    private String generateRefNo() {
        long seq = requestRepository.count() + 1;
        return "TRV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy")) +
               "-" + String.format("%05d", seq);
    }

    // Save a travel request as Draft
    @PostMapping("/draft")
    public ResponseEntity<TravelRequest> saveDraft(@RequestBody TravelRequestDTO dto) {
        if (dto.getEmployeeId() == null) {
            throw new RuntimeException("employeeId is required");
        }

        User user = userRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getEmployeeId()));

        // ---- Role-based transport restrictions ----
        // Employee: can only travel by train or road (no air travel)
        if (user.getRole() == UserRole.employee &&
            dto.getTransportMode() != null &&
            dto.getTransportMode().name().equals("air")) {
            throw new RuntimeException("Employees are not allowed to book air transport");
        }

        // ---- Role-based trip type restrictions ----
        // Employee: can only travel domestic (no international)
        if (user.getRole() == UserRole.employee &&
            dto.getTripType() != null &&
            dto.getTripType() == TripType.international) {
            throw new RuntimeException("Employees can only book domestic travel");
        }

        // Build the travel request object
        TravelRequest request = new TravelRequest();
        request.setReferenceNo(generateRefNo());
        request.setRequester(user);
        request.setTripType(dto.getTripType());
        request.setDestination(dto.getDestination());
        request.setStartingPlace(dto.getStartingPlace());  // new field: where travel starts from
        request.setPurpose(dto.getPurpose());
        request.setEstimatedBudget(dto.getEstimatedBudget());
        request.setTravelFrom(dto.getTravelFrom());
        request.setTravelTo(dto.getTravelTo());
        request.setTransportMode(dto.getTransportMode());
        request.setTravelClass(dto.getTravelClass());       // new field: train class
        request.setStatus(TripStatus.draft);

        // Automatically assign the employee's manager as the approver
        if (user.getManager() != null) {
            request.setAssignedManager(user.getManager());
        }

        return ResponseEntity.ok(requestRepository.save(request));
    }

    // Submit a draft — changes status to manager_pending
    @PostMapping("/{id}/submit")
    public ResponseEntity<TravelRequest> submitRequest(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.submitRequest(id));
    }

    // Delete a draft request (only drafts can be deleted)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraft(@PathVariable Long id) {
        TravelRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: " + id));
        if (request.getStatus() != TripStatus.draft) {
            throw new RuntimeException("Only draft requests can be deleted");
        }
        requestRepository.delete(request);
        return ResponseEntity.noContent().build();
    }

    // Get all travel requests for a specific user
    @GetMapping("/employee/{userId}")
    public ResponseEntity<List<TravelRequest>> getMyRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(requestRepository.findByRequesterId(userId));
    }

    // Get a single travel request by its ID
    @GetMapping("/{id}")
    public ResponseEntity<TravelRequest> getById(@PathVariable Long id) {
        return ResponseEntity.ok(requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: " + id)));
    }

    // Get all requests — used by admin and finance
    @GetMapping
    public ResponseEntity<List<TravelRequest>> getAll() {
        return ResponseEntity.ok(requestRepository.findAll());
    }

    // Get requests pending manager approval for a specific manager
    @GetMapping("/pending/manager/{managerId}")
    public ResponseEntity<List<TravelRequest>> getPendingForManager(@PathVariable Long managerId) {
        List<TravelRequest> pending = requestRepository
                .findByAssignedManagerId(managerId)
                .stream()
                .filter(r -> r.getStatus() == TripStatus.manager_pending)
                .toList();
        return ResponseEntity.ok(pending);
    }

    // Get all requests pending finance approval
    @GetMapping("/pending/finance")
    public ResponseEntity<List<TravelRequest>> getPendingForFinance() {
        return ResponseEntity.ok(requestRepository.findByStatus(TripStatus.finance_pending));
    }
}