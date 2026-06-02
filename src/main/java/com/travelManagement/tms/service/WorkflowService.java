package com.travelManagement.tms.service;

import com.travelManagement.tms.dto.TravelRequestResponseDTO;

// Service interface for the approval workflow.
// The flow is: draft -> manager_pending -> manager_approved -> finance_pending -> finance_approved
// At any step, the request can be disapproved.
public interface WorkflowService {
    TravelRequestResponseDTO submitRequest(Long requestId);

    TravelRequestResponseDTO approveByManager(Long requestId, Long managerId);

    TravelRequestResponseDTO disapproveByManager(Long requestId, Long managerId, String comment);

    TravelRequestResponseDTO approveByFinance(Long requestId, Long financeId);

    TravelRequestResponseDTO disapproveByFinance(Long requestId, Long financeId, String comment);

    TravelRequestResponseDTO reject(Long requestId, Long rejectedById);
}