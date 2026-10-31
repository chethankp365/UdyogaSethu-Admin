package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.DemoTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface DemoTransactionRepository extends JpaRepository<DemoTransaction, UUID> {
    Page<DemoTransaction> findByScenarioNameContainingIgnoreCaseOrTransactionCodeContainingIgnoreCase(
            String scenario, String code, Pageable pageable);
}
