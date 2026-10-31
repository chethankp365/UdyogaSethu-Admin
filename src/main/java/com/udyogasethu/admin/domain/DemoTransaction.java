package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "demo_transactions", schema = "admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemoTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String transactionCode;

    @Column(nullable = false)
    private String scenarioName;

    @Column(nullable = false)
    private Long amountPaise;

    @Column(nullable = false)
    private String status;

    private UUID createdByAdmin;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
