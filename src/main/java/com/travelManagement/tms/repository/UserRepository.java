package com.travelManagement.tms.repository;

import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByManagerId(Long managerId);
    List<User> findByIsActiveTrue();
}
