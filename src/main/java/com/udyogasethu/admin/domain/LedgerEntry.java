package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID walletId;

    @Column(nullable = false)
    private String direction; // credit, debit

    @Column(nullable = false)
    private String bucket; // available, pending, escrow, reward

    @Column(nullable = false)
    private Long amountPaise;

    @Transient
    private String entryType;

    private String refType;

    private UUID refId;

    @Transient
    private Long balanceAfterPaise;

    private String note;

    private String idempotencyKey;

    @Column(name = "is_demo")
    private Boolean isDemo;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
