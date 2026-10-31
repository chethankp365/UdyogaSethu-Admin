package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "worker_profiles", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerProfile {

    @Id
    private UUID userId;

    private String seekerType; // informal, student, part_time

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Long hourlyRatePaise;

    private Long dailyRatePaise;

    private Boolean hasDrivingLicence;

    private BigDecimal ratingAvg;

    @Column(name = "jobs_completed")
    private Integer jobsCompletedCount;

    private Boolean isVerifiedBadge;
}
