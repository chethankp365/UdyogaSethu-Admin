package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profiles", schema = "public")
@org.hibernate.annotations.DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    private UUID id;

    @Column(unique = true, updatable = false)
    private String phone;

    @Column(updatable = false)
    private String fullName;

    @Column(updatable = false)
    private String language; // en, kn, hi, te, ta, mr, bn, or

    @Column(updatable = false)
    private String activeRole; // seeker, provider, farmer, admin

    @Column(updatable = false)
    private String avatarPath;

    @Column(updatable = false)
    private Long dailyEarningsGoalPaise;

    @Column(nullable = false)
    private boolean isSuspended = false;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();
}
