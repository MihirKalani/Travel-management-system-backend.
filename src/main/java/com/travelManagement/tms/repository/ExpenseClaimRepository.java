package com.travelManagement.tms.repository;

import com.travelManagement.tms.entity.ExpenseClaim;
import com.travelManagement.tms.entity.enums.ReimbursementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, Long> {
    Optional<ExpenseClaim> findByTravelRequestId(Long travelRequestId);
    List<ExpenseClaim> findByReimbursementStatus(ReimbursementStatus status);
    List<ExpenseClaim> findBySubmittedById(Long userId);
}
