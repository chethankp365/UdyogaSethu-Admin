package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID listingId;

    private String name;

    private String unit;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private Long unitPricePaise;

    @Column(name = "subtotal_paise", nullable = false)
    private Long totalPricePaise;
}
