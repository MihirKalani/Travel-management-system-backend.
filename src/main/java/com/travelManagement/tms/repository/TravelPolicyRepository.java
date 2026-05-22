package com.travelManagement.tms.repository;

import com.travelManagement.tms.entity.TravelPolicy;
import com.travelManagement.tms.entity.enums.TripType;
import com.travelManagement.tms.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelPolicyRepository extends JpaRepository<TravelPolicy, Long> {
    Optional<TravelPolicy> findByRoleAndTripTypeAndIsActiveTrue(UserRole role, TripType tripType);
}
