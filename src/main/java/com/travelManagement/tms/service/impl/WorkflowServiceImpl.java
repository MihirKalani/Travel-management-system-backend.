package com.travelManagement.tms.service.impl;

import com.travelManagement.tms.dto.DepartmentDTO;
import com.travelManagement.tms.dto.TravelRequestResponseDTO;
import com.travelManagement.tms.dto.UserResponseDTO;
import com.travelManagement.tms.entity.Department;
import com.travelManagement.tms.entity.TravelRequest;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.TripStatus;
import com.travelManagement.tms.repository.TravelRequestRepository;
import com.travelManagement.tms.repository.UserRepository;
import com.travelManagement.tms.service.WorkflowService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// This service handles the approval workflow for travel requests.
// The flow is: draft -> manager_pending -> manager_approved -> finance_pending -> finance_approved
// At any step, the request can be disapproved.
@Service
public class WorkflowServiceImpl implements WorkflowService {

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

    // ──── Service Methods ────

    // Employee submits the request — it goes to manager for approval
    // If the submitter is a manager/admin, it skips manager approval and goes to finance
    @Transactional
    @Override
    public TravelRequestResponseDTO submitRequest(@NonNull Long requestId) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        var requester = request.getRequester();

        if (requester.getRole() == com.travelManagement.tms.entity.enums.UserRole.manager ||
            requester.getRole() == com.travelManagement.tms.entity.enums.UserRole.admin ||
            request.getAssignedManager() == null) {

            // Skip manager and go directly to finance
            request.setStatus(TripStatus.finance_pending);
            request.setSubmittedAt(LocalDateTime.now());

        } else {
            // Normal employee flow: go to manager_pending
            request.setStatus(TripStatus.manager_pending);
            request.setSubmittedAt(LocalDateTime.now());
        }

        TravelRequest saved = requestRepository.save(request);
        return toTravelRequestResponseDTO(saved);
    }

    // Manager approves the request — it goes to finance next
    @Transactional
    @Override
    public TravelRequestResponseDTO approveByManager(@NonNull Long requestId, @NonNull Long managerId) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        // Move from manager_pending to manager_approved, then to finance_pending
        request.setStatus(TripStatus.finance_pending);

        TravelRequest saved = requestRepository.save(request);
        return toTravelRequestResponseDTO(saved);
    }

    // Manager disapproves the request
    @Transactional
    @Override
    public TravelRequestResponseDTO disapproveByManager(@NonNull Long requestId, @NonNull Long managerId, String comment) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.manager_disapproved);
        request.setRejectedAt(LocalDateTime.now());

        // Save the reason for disapproval
        if (comment != null && !comment.isBlank()) {
            request.setOverrideNote(comment);
        }

        TravelRequest saved = requestRepository.save(request);
        return toTravelRequestResponseDTO(saved);
    }

    // Finance approves the request — travel is fully approved!
    @Transactional
    @Override
    public TravelRequestResponseDTO approveByFinance(@NonNull Long requestId, @NonNull Long financeId) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.finance_approved);
        request.setApprovedAt(LocalDateTime.now());

        TravelRequest saved = requestRepository.save(request);
        return toTravelRequestResponseDTO(saved);
    }

    // Finance disapproves the request
    @Transactional
    @Override
    public TravelRequestResponseDTO disapproveByFinance(@NonNull Long requestId, @NonNull Long financeId, String comment) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.finance_disapproved);
        request.setRejectedAt(LocalDateTime.now());

        if (comment != null && !comment.isBlank()) {
            request.setOverrideNote(comment);
        }

        TravelRequest saved = requestRepository.save(request);
        return toTravelRequestResponseDTO(saved);
    }

    // Old reject method — kept for backward compatibility
    @Transactional
    @Override
    public TravelRequestResponseDTO reject(@NonNull Long requestId, @NonNull Long rejectedById) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.manager_disapproved);
        request.setRejectedAt(LocalDateTime.now());

        TravelRequest saved = requestRepository.save(request);
        return toTravelRequestResponseDTO(saved);
    }
}
