package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.JobAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobAssignmentRepository extends JpaRepository<JobAssignment, UUID> {
    List<JobAssignment> findByJobId(UUID jobId);
    Page<JobAssignment> findByStatusIgnoreCase(String status, Pageable pageable);
    long countByStatusIgnoreCase(String status);
}
