package com.travelManagement.tms.service;

import com.travelManagement.tms.entity.Notification;
import com.travelManagement.tms.entity.TravelRequest;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.TripStatus;
import com.travelManagement.tms.repository.NotificationRepository;
import com.travelManagement.tms.repository.TravelRequestRepository;
import com.travelManagement.tms.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// This service handles the approval workflow for travel requests.
// The flow is: draft -> manager_pending -> manager_approved -> finance_pending -> finance_approved
// At any step, the request can be disapproved.
@Service
public class WorkflowService {

    @Autowired
    private TravelRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private NotificationRepository notificationRepository;

    // Employee submits the request — it goes to manager for approval
    // If the submitter is a manager/admin, it skips manager approval and goes to finance
    @Transactional
    public TravelRequest submitRequest(@NonNull Long requestId) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        User requester = request.getRequester();

        if (requester.getRole() == com.travelManagement.tms.entity.enums.UserRole.manager || 
            requester.getRole() == com.travelManagement.tms.entity.enums.UserRole.admin || 
            request.getAssignedManager() == null) {
            
            // Skip manager and go directly to finance
            request.setStatus(TripStatus.finance_pending);
            request.setSubmittedAt(LocalDateTime.now());
            auditService.logAction("travel_requests", requestId, "trip_submitted", requester.getId());

        } else {
            // Normal employee flow: go to manager_pending
            request.setStatus(TripStatus.manager_pending);
            request.setSubmittedAt(LocalDateTime.now());

            // Log this action for audit trail
            auditService.logAction("travel_requests", requestId, "trip_submitted", requester.getId());

            // Notify the manager that a new request is waiting for their approval
            createNotification(
                request.getAssignedManager(),
                "New Travel Request",
                "A travel request (" + request.getReferenceNo() + ") needs your approval.",
                "travel_requests",
                requestId
            );
        }

        return requestRepository.save(request);
    }

    // Manager approves the request — it goes to finance next
    @Transactional
    public TravelRequest approveByManager(@NonNull Long requestId, @NonNull Long managerId) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        // Move from manager_pending to manager_approved, then to finance_pending
        request.setStatus(TripStatus.finance_pending);

        auditService.logAction("travel_requests", requestId, "manager_approved", managerId);

        // Notify the employee that their request was approved by manager
        createNotification(
            request.getRequester(),
            "Request Approved by Manager",
            "Your travel request (" + request.getReferenceNo() + ") has been approved by your manager. It is now pending finance approval.",
            "travel_requests",
            requestId
        );

        return requestRepository.save(request);
    }

    // Manager disapproves the request
    @Transactional
    public TravelRequest disapproveByManager(@NonNull Long requestId, @NonNull Long managerId, String comment) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.manager_disapproved);
        request.setRejectedAt(LocalDateTime.now());

        // Save the reason for disapproval
        if (comment != null && !comment.isBlank()) {
            request.setOverrideNote(comment);
        }

        auditService.logAction("travel_requests", requestId, "manager_disapproved", managerId);

        // Notify the employee that their request was disapproved
        createNotification(
            request.getRequester(),
            "Request Disapproved by Manager",
            "Your travel request (" + request.getReferenceNo() + ") was not approved by your manager. You can submit a new request.",
            "travel_requests",
            requestId
        );

        return requestRepository.save(request);
    }

    // Finance approves the request — travel is fully approved!
    @Transactional
    public TravelRequest approveByFinance(@NonNull Long requestId, @NonNull Long financeId) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.finance_approved);
        request.setApprovedAt(LocalDateTime.now());

        auditService.logAction("travel_requests", requestId, "finance_approved", financeId);

        // Notify the employee that their travel is fully approved
        createNotification(
            request.getRequester(),
            "Travel Request Fully Approved!",
            "Your travel request (" + request.getReferenceNo() + ") has been approved by finance. You are good to go!",
            "travel_requests",
            requestId
        );

        return requestRepository.save(request);
    }

    // Finance disapproves the request
    @Transactional
    public TravelRequest disapproveByFinance(@NonNull Long requestId, @NonNull Long financeId, String comment) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.finance_disapproved);
        request.setRejectedAt(LocalDateTime.now());

        if (comment != null && !comment.isBlank()) {
            request.setOverrideNote(comment);
        }

        auditService.logAction("travel_requests", requestId, "finance_disapproved", financeId);

        // Notify the employee about the disapproval
        createNotification(
            request.getRequester(),
            "Request Disapproved by Finance",
            "Your travel request (" + request.getReferenceNo() + ") was not approved by finance. You can submit a new request.",
            "travel_requests",
            requestId
        );

        return requestRepository.save(request);
    }

    // Old reject method — kept for backward compatibility
    @Transactional
    public TravelRequest reject(@NonNull Long requestId, @NonNull Long rejectedById) {
        TravelRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        request.setStatus(TripStatus.manager_disapproved);
        request.setRejectedAt(LocalDateTime.now());
        auditService.logAction("travel_requests", requestId, "trip_rejected", rejectedById);
        return requestRepository.save(request);
    }

    // Helper method to create a notification for a user
    private void createNotification(User user, String title, String body, String relatedEntity, Long relatedId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setRelatedEntity(relatedEntity);
        notification.setRelatedId(relatedId);
        notificationRepository.save(notification);
    }
}