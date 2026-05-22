package com.travelManagement.tms.repository;

import com.travelManagement.tms.entity.ApprovalStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {
    List<ApprovalStep> findByTravelRequestId(Long travelRequestId);
    Optional<ApprovalStep> findByTravelRequestIdAndLevel(Long travelRequestId, Short level);
}
