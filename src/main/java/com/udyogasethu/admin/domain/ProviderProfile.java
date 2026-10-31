package com.udyogasethu.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "provider_profiles", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderProfile {

    @Id
    private UUID userId;

    private String providerType; // household, employer

    private Integer typicalCrewSize;

    private Boolean isVerifiedBadge;
}
