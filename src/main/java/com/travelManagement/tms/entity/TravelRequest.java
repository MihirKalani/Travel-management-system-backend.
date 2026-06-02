package com.travelManagement.tms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.travelManagement.tms.entity.enums.TravelClass;
import com.travelManagement.tms.entity.enums.TripStatus;
import com.travelManagement.tms.entity.enums.TripType;
import com.travelManagement.tms.entity.enums.TransportMode;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// This table stores all the travel requests created by employees and managers.
@Entity
@Table(name = "travel_requests")
@Data
public class TravelRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique reference number like TRV-2026-00001
    @Column(name = "reference_no", nullable = false, unique = true)
    private String referenceNo;

    // The user who created this travel request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @JsonIgnoreProperties({"manager", "passwordHash", "hibernateLazyInitializer", "handler"})
    private User requester;

    // Is this a domestic or international trip
    @Enumerated(EnumType.STRING)
    @Column(name = "trip_type", nullable = false)
    private TripType tripType;

    // Where the employee is going
    @Column(nullable = false)
    private String destination;

    // Where the employee is starting from (their current city/location)
    @Column(name = "starting_place")
    private String startingPlace;

    // Why they need to travel
    @Column(nullable = false)
    private String purpose;

    // Travel date range
    @Column(name = "travel_from", nullable = false)
    private LocalDate travelFrom;

    @Column(name = "travel_to", nullable = false)
    private LocalDate travelTo;

    // How they want to travel: train, road, or air
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_mode", nullable = false)
    private TransportMode transportMode;

    // If traveling by train, which class: first_class, second_class, sleeper, general
    @Enumerated(EnumType.STRING)
    @Column(name = "travel_class")
    private TravelClass travelClass;

    // How much money they think they'll need
    @Column(name = "estimated_budget", nullable = false)
    private BigDecimal estimatedBudget;

    // How much was actually approved by manager/finance
    @Column(name = "approved_budget")
    private BigDecimal approvedBudget;

    // Current status of this request in the approval pipeline
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status = TripStatus.draft;

    // The manager who is assigned to approve this request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_manager_id")
    @JsonIgnoreProperties({"manager", "passwordHash", "hibernateLazyInitializer", "handler"})
    private User assignedManager;

    @Column(name = "override_note")
    private String overrideNote;

    // Timestamps for different stages
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