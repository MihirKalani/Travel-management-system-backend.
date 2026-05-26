package com.travelManagement.tms.entity.enums;

// Status of a travel request as it moves through the approval process
// Flow: draft -> manager_pending -> manager_approved -> finance_pending -> finance_approved
// At any step, the approver can disapprove the request
public enum TripStatus {
    draft,
    manager_pending,
    manager_approved,
    finance_pending,
    finance_approved,
    manager_disapproved,
    finance_disapproved,
    cancelled,
    completed
}
