package com.travelManagement.tms.dto;

import com.travelManagement.tms.entity.enums.TravelClass;
import com.travelManagement.tms.entity.enums.TransportMode;
import com.travelManagement.tms.entity.enums.TripStatus;
import com.travelManagement.tms.entity.enums.TripType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Response DTO for TravelRequest entity.
// Matches the JSON shape the frontend already expects.
@Data
public class TravelRequestResponseDTO {
    private Long id;
    private String referenceNo;
    private UserResponseDTO.ManagerDTO requester;
    private TripType tripType;
    private String destination;
    private String startingPlace;
    private String purpose;
    private LocalDate travelFrom;
    private LocalDate travelTo;
    private TransportMode transportMode;
    private TravelClass travelClass;
    private BigDecimal estimatedBudget;
    private BigDecimal approvedBudget;
    private TripStatus status;
    private UserResponseDTO.ManagerDTO assignedManager;
    private String overrideNote;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
