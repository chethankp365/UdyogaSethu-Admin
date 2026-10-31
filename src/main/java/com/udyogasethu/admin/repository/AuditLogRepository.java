package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByTargetTypeIgnoreCase(String targetType, Pageable pageable);
    Page<AuditLog> findByAdminEmailContainingIgnoreCaseOrActionContainingIgnoreCaseOrTargetTypeContainingIgnoreCase(
            String email, String action, String targetType, Pageable pageable);
}
