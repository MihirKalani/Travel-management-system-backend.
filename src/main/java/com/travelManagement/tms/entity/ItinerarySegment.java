package com.travelManagement.tms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.travelManagement.tms.entity.enums.TransportMode;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "itinerary_segments")
@Data
public class ItinerarySegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_request_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TravelRequest travelRequest;

    @Column(name = "segment_order", nullable = false)
    private Short segmentOrder;

    /** 'travel' or 'stay' */
    @Column(name = "segment_type", nullable = false)
    private String segmentType;

    // Travel segment fields
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_mode")
    private TransportMode transportMode;

    @Column(name = "from_location")
    private String fromLocation;

    @Column(name = "to_location")
    private String toLocation;

    @Column(name = "departure_at")
    private LocalDateTime departureAt;

    @Column(name = "arrival_at")
    private LocalDateTime arrivalAt;

    private String carrier;

    // Stay fields
    @Column(name = "property_name")
    private String propertyName;

    @Column(name = "property_address")
    private String propertyAddress;

    @Column(name = "check_in")
    private LocalDate checkIn;

    @Column(name = "check_out")
    private LocalDate checkOut;

    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
