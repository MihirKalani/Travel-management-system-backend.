package com.travelManagement.tms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.travelManagement.tms.entity.enums.ReimbursementStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// This table stores the expense claims that employees submit after a trip.
// Each claim is linked to one travel request.
@Entity
@Table(name = "expense_claims")
@Data
public class ExpenseClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which travel request is this expense claim for
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_request_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TravelRequest travelRequest;

    // Who submitted this expense claim
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", nullable = false)
    @JsonIgnoreProperties({"manager", "passwordHash", "hibernateLazyInitializer", "handler"})
    private User submittedBy;

    // Current status of the reimbursement process
    @Enumerated(EnumType.STRING)
    @Column(name = "reimbursement_status", nullable = false)
    private ReimbursementStatus reimbursementStatus = ReimbursementStatus.not_submitted;

    // Total amount the employee is claiming
    @Column(name = "total_claimed", nullable = false)
    private BigDecimal totalClaimed = BigDecimal.ZERO;

    // Total amount approved by finance
    @Column(name = "total_approved")
    private BigDecimal totalApproved;

    // The actual PDF bill uploaded by the employee
    // Stored as a BLOB (binary data) directly in the database
    @Lob
    @Column(name = "bill_pdf", columnDefinition = "LONGBLOB")
    @JsonIgnore // don't send the raw bytes in JSON responses
    private byte[] billPdf;

    // Original name of the uploaded PDF file (like "hotel_receipt.pdf")
    @Column(name = "bill_file_name")
    private String billFileName;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
