package com.travelManagement.tms.dto;

import com.travelManagement.tms.entity.enums.TransportMode;
import com.travelManagement.tms.entity.enums.TripType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TravelRequestDTO {
    private Long employeeId;
    private TripType tripType;
    private String destination;
    private String purpose;
    private BigDecimal estimatedBudget;
    private LocalDate travelFrom;
    private LocalDate travelTo;
    private TransportMode transportMode;
}