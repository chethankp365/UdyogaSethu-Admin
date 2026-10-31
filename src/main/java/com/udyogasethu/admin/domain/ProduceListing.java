package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "produce_listings", schema = "public")
@org.hibernate.annotations.DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduceListing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID farmerId;

    @Column(name = "name", nullable = false, updatable = false)
    private String cropName;

    @Column(updatable = false)
    private String category; // vegetable, fruit, grain, dairy, flower, other

    @Column(name = "price_paise", updatable = false)
    private Long pricePerUnitPaise;

    @Column(updatable = false)
    private String unit; // kg, quintal, litre

    @Column(updatable = false)
    private BigDecimal stockQty;

    @Column(updatable = false)
    private BigDecimal reservedQty;

    @Column(name = "harvest_at", updatable = false)
    private Instant harvestDate;

    @Column(updatable = false)
    private String farmingMethod;

    @Column(nullable = false)
    private String status; // active, low_stock, sold_out, paused

    @Column(updatable = false)
    private Instant createdAt = Instant.now();
}
