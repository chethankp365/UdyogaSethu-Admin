package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "kyc_records", schema = "public")
@org.hibernate.annotations.DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, updatable = false)
    private String docType; // aadhaar, pan, driving_licence, college_id

    @Column(name = "masked_id", updatable = false)
    private String docNumberMasked;

    @Column(name = "doc_path", updatable = false)
    private String docPhotoPath;

    @Column(nullable = false)
    private String status; // not_started, pending, verified, rejected, review_required

    private String rejectionReason;

    private UUID reviewerId;

    private Instant verifiedAt;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();
}
