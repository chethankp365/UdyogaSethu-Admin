package com.udyogasethu.admin.service;

import com.udyogasethu.admin.domain.AuditLog;
import com.udyogasethu.admin.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public AuditLog recordAudit(String action, String targetType, String targetId, String reason, String diffJson) {
        String adminEmail = "system@udyogasethu.org";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            adminEmail = auth.getName();
        }

        AuditLog logEntry = AuditLog.builder()
                .adminEmail(adminEmail)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .reason(reason)
                .diffJson(diffJson)
                .createdAt(Instant.now())
                .build();

        AuditLog saved = auditLogRepository.save(logEntry);
        log.info("[AUDIT] Action: {}, Target: {}/{}, By: {}, Reason: {}", action, targetType, targetId, adminEmail, reason);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return auditLogRepository.findAll(pageable);
        }
        return auditLogRepository.findByAdminEmailContainingIgnoreCaseOrActionContainingIgnoreCaseOrTargetTypeContainingIgnoreCase(
                search, search, search, pageable);
    }
}
