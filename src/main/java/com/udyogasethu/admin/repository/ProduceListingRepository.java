package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.ProduceListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProduceListingRepository extends JpaRepository<ProduceListing, UUID> {
    Page<ProduceListing> findByCropNameContainingIgnoreCase(String cropName, Pageable pageable);
    Page<ProduceListing> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<ProduceListing> findByStatusIgnoreCase(String status, Pageable pageable);
    long countByStatusIgnoreCase(String status);
}
