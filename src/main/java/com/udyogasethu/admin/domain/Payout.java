package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payouts", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID walletId;

    @Column(nullable = false)
    private Long amountPaise;

    private String method; // upi, bank_transfer

    @Column(nullable = false)
    private String status; // requested, processing, completed, failed, cancelled

    @Column(name = "payout_ref")
    private String utrNumber;

    private Instant createdAt = Instant.now();
}
