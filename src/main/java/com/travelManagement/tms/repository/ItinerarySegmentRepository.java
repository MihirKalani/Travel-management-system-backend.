package com.travelManagement.tms.repository;

import com.travelManagement.tms.entity.ItinerarySegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItinerarySegmentRepository extends JpaRepository<ItinerarySegment, Long> {
    List<ItinerarySegment> findByTravelRequestIdOrderBySegmentOrder(Long travelRequestId);
}
