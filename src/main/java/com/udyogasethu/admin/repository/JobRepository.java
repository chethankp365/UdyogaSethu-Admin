package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    Page<Job> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Job> findByStatusIgnoreCase(String status, Pageable pageable);
    Page<Job> findByTitleContainingIgnoreCaseAndStatusIgnoreCase(String title, String status, Pageable pageable);
    long countByStatusIgnoreCase(String status);
}
