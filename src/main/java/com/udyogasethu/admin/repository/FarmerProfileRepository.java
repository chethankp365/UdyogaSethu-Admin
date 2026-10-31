package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.FarmerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, UUID> {
    Page<FarmerProfile> findByFarmNameContainingIgnoreCaseOrClusterNameContainingIgnoreCase(
            String farmName, String clusterName, Pageable pageable);
}
