package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "farmer_profiles", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerProfile {

    @Id
    private UUID userId;

    private String farmName;

    @Column(name = "cluster_village")
    private String clusterName;

    @Column(name = "acres")
    private BigDecimal totalLandAcres;

    private BigDecimal deliveryRadiusKm;

    private Boolean pickupAvailable;

    private String organicCertNumber;
}
