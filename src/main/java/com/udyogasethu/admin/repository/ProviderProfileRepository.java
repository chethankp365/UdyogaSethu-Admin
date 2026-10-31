package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.ProviderProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, UUID> {
    Page<ProviderProfile> findByProviderTypeIgnoreCase(String providerType, Pageable pageable);
}
