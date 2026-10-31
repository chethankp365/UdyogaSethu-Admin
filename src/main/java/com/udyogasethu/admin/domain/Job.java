package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jobs", schema = "public")
@org.hibernate.annotations.DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(updatable = false)
    private UUID providerId;

    @Column(updatable = false)
    private UUID categoryId;

    @Column(nullable = false, updatable = false)
    private String title;

    @Column(updatable = false)
    private String wageType; // daily, hourly, fixed

    @Column(updatable = false)
    private Long wagePaise;

    @Column(updatable = false)
    private Integer slotsTotal;

    @Column(updatable = false)
    private Integer slotsFilled;

    @Column(updatable = false)
    private Instant startAt;

    @Column(name = "geofence_radius_m", updatable = false)
    private Integer geofenceRadiusM; // default 150

    @Column(nullable = false)
    private String status; // draft, open, filled, closed, cancelled

    @Column(updatable = false)
    private String thumbnailPath;

    @Column(name = "is_demo", updatable = false)
    private Boolean isDemo;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();
}
