package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String orderCode;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID farmerId;

    private Long totalPaise;

    @Column(name = "fee_paise")
    private Long platformFeePaise;

    @Column(nullable = false)
    private String status; // pending, accepted, packed, out_for_delivery, delivered, cancelled, disputed

    @Column(name = "delivery_pin")
    private String handoverPin;

    private Instant createdAt = Instant.now();
}
