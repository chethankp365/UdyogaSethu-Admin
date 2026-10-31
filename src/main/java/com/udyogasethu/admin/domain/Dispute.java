package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "disputes", schema = "public")
@org.hibernate.annotations.DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ref_type", updatable = false)
    private String refType;

    @Column(name = "ref_id", updatable = false)
    private UUID assignmentId;

    @Column(name = "raised_by", nullable = false, updatable = false)
    private UUID complainantId;

    @Column(name = "against", updatable = false)
    private UUID respondentId;

    @Column(name = "issue_type", updatable = false)
    private String category;

    @Column(name = "details", columnDefinition = "TEXT", updatable = false)
    private String reason;

    @Column(nullable = false)
    private String status; // open, under_review, escalated, resolved, rejected

    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "refund_paise")
    private Long refundAmountPaise;

    private UUID resolvedBy;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();
}
