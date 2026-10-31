package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs", schema = "admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID adminId;

    @Column(nullable = false)
    private String adminEmail;

    @Column(nullable = false)
    private String action; // USER_SUSPENDED, JOB_CANCELLED, KYC_APPROVED, etc.

    @Column(nullable = false)
    private String targetType; // PROFILE, JOB, LISTING, KYC, DISPUTE

    @Column(nullable = false)
    private String targetId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String diffJson;

    private String ipAddress;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
