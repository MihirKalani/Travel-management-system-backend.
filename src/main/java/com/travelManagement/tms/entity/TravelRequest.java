package com.travelManagement.tms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.travelManagement.tms.entity.enums.TripStatus;
import com.travelManagement.tms.entity.enums.TripType;
import com.travelManagement.tms.entity.enums.TransportMode;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "travel_requests")
@Data
public class TravelRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_no", nullable = false, unique = true)
    private String referenceNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @JsonIgnoreProperties({"manager", "passwordHash", "hibernateLazyInitializer", "handler"})
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_type", nullable = false)
    private TripType tripType;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String purpose;

    @Column(name = "travel_from", nullable = false)
    private LocalDate travelFrom;

    @Column(name = "travel_to", nullable = false)
    private LocalDate travelTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_mode", nullable = false)
    private TransportMode transportMode;

    @Column(name = "estimated_budget", nullable = false)
    private BigDecimal estimatedBudget;

    @Column(name = "approved_budget")
    private BigDecimal approvedBudget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status = TripStatus.draft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_manager_id")
    @JsonIgnoreProperties({"manager", "passwordHash", "hibernateLazyInitializer", "handler"})
    private User assignedManager;

    @Column(name = "has_policy_violation", nullable = false)
    private Boolean hasPolicyViolation = false;

    @Column(name = "policy_violation_notes")
    private String policyViolationNotes;

    @Column(name = "override_note")
    private String overrideNote;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}