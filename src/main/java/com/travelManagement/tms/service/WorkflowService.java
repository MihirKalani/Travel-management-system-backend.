package com.travelManagement.tms.service;

import com.travelManagement.tms.entity.TravelRequest;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.TripStatus;
import com.travelManagement.tms.repository.TravelRequestRepository;
import com.travelManagement.tms.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WorkflowService {

    @Autowired
    private TravelRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditService auditService;

    @Transactional
    public TravelRequest submitRequest(@NonNull Long requestId) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.pending_manager);
        request.setSubmittedAt(LocalDateTime.now());

        auditService.logAction("travel_requests", requestId, "trip_submitted",
                request.getRequester().getId());
        return requestRepository.save(request);
    }

    @Transactional
    public TravelRequest approveByManager(@NonNull Long requestId, @NonNull Long managerId) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.pending_finance);
        auditService.logAction("travel_requests", requestId, "trip_approved", managerId);
        return requestRepository.save(request);
    }

    @Transactional
    public TravelRequest approveByFinance(@NonNull Long requestId, @NonNull Long financeId) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.approved);
        request.setApprovedAt(LocalDateTime.now());
        auditService.logAction("travel_requests", requestId, "trip_approved", financeId);
        return requestRepository.save(request);
    }

    @Transactional
    public TravelRequest reject(@NonNull Long requestId, @NonNull Long rejectedById) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.rejected);
        request.setRejectedAt(LocalDateTime.now());
        auditService.logAction("travel_requests", requestId, "trip_rejected", rejectedById);
        return requestRepository.save(request);
    }
}