package com.travelManagement.tms.service;

import com.travelManagement.tms.entity.AuditLog;
import com.travelManagement.tms.entity.User;
import com.travelManagement.tms.entity.enums.UserRole;
import com.travelManagement.tms.repository.AuditLogRepository;
import com.travelManagement.tms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    public void logAction(String targetEntity, Long targetId, String action, Long actorId) {
        AuditLog log = new AuditLog();
        log.setTargetEntity(targetEntity);
        log.setTargetId(targetId);
        log.setAction(action);

        if (actorId != null) {
            userRepository.findById(actorId).ifPresent(actor -> {
                log.setActor(actor);
                log.setActorRole(actor.getRole());
            });
        }

        auditLogRepository.save(log);
    }
}