package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.AppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, UUID> {
    Page<AppNotification> findByUserId(UUID userId, Pageable pageable);
    Page<AppNotification> findByReadAtIsNull(Pageable pageable);
    Page<AppNotification> findByReadAtIsNotNull(Pageable pageable);
    long countByReadAtIsNull();
}
