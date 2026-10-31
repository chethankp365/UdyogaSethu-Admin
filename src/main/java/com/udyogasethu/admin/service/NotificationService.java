package com.udyogasethu.admin.service;

import com.udyogasethu.admin.domain.AppNotification;
import com.udyogasethu.admin.repository.AppNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final AppNotificationRepository appNotificationRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Page<AppNotification> getNotifications(Pageable pageable) {
        return appNotificationRepository.findAll(pageable);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public AppNotification broadcastNotification(UUID targetUserId, String title, String body, String type) {
        AppNotification notification = AppNotification.builder()
                .userId(targetUserId)
                .title(title)
                .body(body)
                .type(type != null ? type : "broadcast")
                .readAt(null)
                .createdAt(Instant.now())
                .build();

        AppNotification saved = appNotificationRepository.save(notification);
        auditLogService.recordAudit("SEND_NOTIFICATION", "NOTIFICATION", saved.getId().toString(),
                "Title: " + title, null);

        return saved;
    }
}
