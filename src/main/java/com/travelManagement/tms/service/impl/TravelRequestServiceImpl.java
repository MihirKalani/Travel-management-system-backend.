package com.travelManagement.tms.service.impl;

import com.travelManagement.tms.dto.DepartmentDTO;
import com.travelManagement.tms.dto.TravelRequestDTO;
import com.travelManagement.tms.dto.TravelRequestResponseDTO;
import com.travelManagement.tms.dto.UserResponseDTO;
import com.travelManagement.tms.entity.Department;
import com.travelManagement.tms.entity.TravelRequest;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.TripStatus;
import com.travelManagement.tms.entity.enums.TripType;
import com.travelManagement.tms.entity.enums.UserRole;
import com.travelManagement.tms.repository.TravelRequestRepository;
import com.travelManagement.tms.repository.UserRepository;
import com.travelManagement.tms.service.TravelRequestService;
import com.travelManagement.tms.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelRequestServiceImpl implements TravelRequestService {

    @Autowired
    private TravelRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkflowService workflowService;

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

    private List<TravelRequestResponseDTO> toTravelRequestResponseDTOList(List<TravelRequest> list) {
        return list.stream().map(this::toTravelRequestResponseDTO).collect(Collectors.toList());
    }

    // ──── Service Methods ────

    // Generate a reference number like TRV-2026-00001
    private String generateRefNo() {
        long seq = requestRepository.count() + 1;
        return "TRV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy")) +
               "-" + String.format("%05d", seq);
    }

    // Save a travel request as Draft
    @Override
    public TravelRequestResponseDTO saveDraft(TravelRequestDTO dto) {
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
        request.setStartingPlace(dto.getStartingPlace());
        request.setPurpose(dto.getPurpose());
        request.setEstimatedBudget(dto.getEstimatedBudget());
        request.setTravelFrom(dto.getTravelFrom());
        request.setTravelTo(dto.getTravelTo());
        request.setTransportMode(dto.getTransportMode());
        request.setTravelClass(dto.getTravelClass());
        request.setStatus(TripStatus.draft);

        // Automatically assign the employee's manager as the approver
        if (user.getManager() != null) {
            request.setAssignedManager(user.getManager());
        }

        TravelRequest saved = requestRepository.save(request);
        return toTravelRequestResponseDTO(saved);
    }

    // Submit a draft — delegates to WorkflowService
    @Override
    public TravelRequestResponseDTO submitRequest(Long id) {
        return workflowService.submitRequest(id);
    }

    // Delete a draft request (only drafts can be deleted)
    @Override
    public void deleteDraft(Long id) {
        TravelRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: " + id));
        if (request.getStatus() != TripStatus.draft) {
            throw new RuntimeException("Only draft requests can be deleted");
        }
        requestRepository.delete(request);
    }

    // Get all travel requests for a specific user
    @Override
    public List<TravelRequestResponseDTO> getMyRequests(Long userId) {
        return toTravelRequestResponseDTOList(requestRepository.findByRequesterId(userId));
    }

    // Get a single travel request by its ID
    @Override
    public TravelRequestResponseDTO getById(Long id) {
        TravelRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: " + id));
        return toTravelRequestResponseDTO(request);
    }

    // Get all requests — used by admin and finance
    @Override
    public List<TravelRequestResponseDTO> getAll() {
        return toTravelRequestResponseDTOList(requestRepository.findAll());
    }

    // Get requests pending manager approval for a specific manager
    @Override
    public List<TravelRequestResponseDTO> getPendingForManager(Long managerId) {
        List<TravelRequest> pending = requestRepository
                .findByAssignedManagerId(managerId)
                .stream()
                .filter(r -> r.getStatus() == TripStatus.manager_pending)
                .toList();
        return toTravelRequestResponseDTOList(pending);
    }

    // Get all requests pending finance approval
    @Override
    public List<TravelRequestResponseDTO> getPendingForFinance() {
        return toTravelRequestResponseDTOList(
                requestRepository.findByStatus(TripStatus.finance_pending));
    }
}
