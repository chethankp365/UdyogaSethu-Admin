package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_assignments", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID jobId;

    @Column(nullable = false)
    private UUID workerId;

    @Transient
    private UUID providerId;

    @Column(unique = true)
    private String bookingCode;

    private Long agreedWagePaise;

    @Column(nullable = false)
    private String status; // requested, accepted, ticket_issued, arrived, in_progress_verified, completed, payment_released, cancelled, no_show, disputed

    private Instant startedAt;

    private Instant endedAt;

    private Integer elapsedSeconds;

    private Boolean paymentEligible;

    private Instant createdAt = Instant.now();
}
