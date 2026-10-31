package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.LedgerEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    Page<LedgerEntry> findByWalletId(UUID walletId, Pageable pageable);
    Page<LedgerEntry> findByDirectionIgnoreCase(String direction, Pageable pageable);
    Page<LedgerEntry> findByBucketIgnoreCase(String bucket, Pageable pageable);
}
