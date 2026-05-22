package com.travelManagement.tms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.travelManagement.tms.entity.enums.ReimbursementStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense_claims")
@Data
public class ExpenseClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_request_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TravelRequest travelRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", nullable = false)
    @JsonIgnoreProperties({"manager", "passwordHash", "hibernateLazyInitializer", "handler"})
    private User submittedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "reimbursement_status", nullable = false)
    private ReimbursementStatus reimbursementStatus = ReimbursementStatus.not_submitted;

    @Column(name = "total_claimed", nullable = false)
    private BigDecimal totalClaimed = BigDecimal.ZERO;

    @Column(name = "total_approved")
    private BigDecimal totalApproved;

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
