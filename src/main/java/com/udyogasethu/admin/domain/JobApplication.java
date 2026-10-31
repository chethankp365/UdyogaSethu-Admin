package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_applications", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID jobId;

    @Column(nullable = false)
    private UUID workerId;

    @Column(nullable = false)
    private String status; // applied, shortlisted, rejected, withdrawn, hired

    @Column(name = "message", columnDefinition = "TEXT")
    private String note;

    private Instant createdAt = Instant.now();

    private Instant updatedAt;
}
