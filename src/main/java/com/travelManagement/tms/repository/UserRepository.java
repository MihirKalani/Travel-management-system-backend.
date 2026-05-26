package com.travelManagement.tms.repository;

import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their email (used for login)
    Optional<User> findByEmail(String email);

    // Find a user by their unique employee code like EMP001
    Optional<User> findByUserCode(String userCode);

    // Get all users with a specific role (like all managers, all employees, etc.)
    List<User> findByRole(UserRole role);

    // Get all employees who report to a specific manager
    List<User> findByManagerId(Long managerId);

    // Get all active users
    List<User> findByIsActiveTrue();
}
