package com.travelManagement.tms.dto;

import com.travelManagement.tms.entity.enums.TravelClass;
import com.travelManagement.tms.entity.enums.TransportMode;
import com.travelManagement.tms.entity.enums.TripType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

// This DTO (Data Transfer Object) is what the frontend sends
// when creating a new travel request.
@Data
public class TravelRequestDTO {
    private Long employeeId;
    private TripType tripType;
    private String destination;
    private String startingPlace;   // where the employee starts travel from
    private String purpose;
    private BigDecimal estimatedBudget;
    private LocalDate travelFrom;
    private LocalDate travelTo;
    private TransportMode transportMode;
    private TravelClass travelClass; // only used when transportMode is train
}