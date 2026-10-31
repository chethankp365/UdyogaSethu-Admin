package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID payerId;

    @Column(nullable = false)
    private Long amountPaise;

    private Long platformFeePaise;

    @Transient
    private Long protectionFeePaise;

    @Column(name = "protected")
    private Boolean isProtected;

    private String method;

    private String refType;

    private UUID refId;

    @Column(nullable = false)
    private String status; // created, authorised, captured, held, released, refunded, failed

    private String razorpayOrderId;

    private String razorpayPaymentId;

    @Column(unique = true)
    private String receiptNo;

    private Instant createdAt = Instant.now();
}
