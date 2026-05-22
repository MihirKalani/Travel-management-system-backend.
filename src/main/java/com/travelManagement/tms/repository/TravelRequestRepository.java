package com.travelManagement.tms.repository;

import com.travelManagement.tms.entity.TravelRequest;
import com.travelManagement.tms.entity.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelRequestRepository extends JpaRepository<TravelRequest, Long> {
    List<TravelRequest> findByRequesterId(Long requesterId);
    List<TravelRequest> findByStatus(TripStatus status);
    List<TravelRequest> findByAssignedManagerId(Long managerId);
    List<TravelRequest> findByRequesterIdAndStatus(Long requesterId, TripStatus status);
}