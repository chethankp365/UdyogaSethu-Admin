package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ratings_reviews", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ref_type")
    private String refType;

    @Column(name = "rater_id", nullable = false)
    private UUID reviewerId;

    @Column(name = "target_id", nullable = false)
    private UUID revieweeId;

    @Column(name = "ref_id")
    private UUID assignmentId;

    @Column(name = "stars", nullable = false)
    private Integer rating; // 1 to 5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "hidden")
    private boolean isHidden = false;

    private Instant createdAt = Instant.now();
}
