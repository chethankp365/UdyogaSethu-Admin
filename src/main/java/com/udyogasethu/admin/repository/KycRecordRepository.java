package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.KycRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface KycRecordRepository extends JpaRepository<KycRecord, UUID> {
    Page<KycRecord> findByStatusIgnoreCase(String status, Pageable pageable);
    long countByStatusIgnoreCase(String status);
}
