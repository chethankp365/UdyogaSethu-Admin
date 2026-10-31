package com.udyogasethu.admin.service;

import com.udyogasethu.admin.domain.Job;
import com.udyogasethu.admin.domain.JobApplication;
import com.udyogasethu.admin.domain.JobAssignment;
import com.udyogasethu.admin.repository.JobApplicationRepository;
import com.udyogasethu.admin.repository.JobAssignmentRepository;
import com.udyogasethu.admin.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobAssignmentRepository jobAssignmentRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Page<Job> getJobs(String titleSearch, String status, Pageable pageable) {
        if (titleSearch != null && !titleSearch.trim().isEmpty() && status != null && !status.equalsIgnoreCase("ALL")) {
            return jobRepository.findByTitleContainingIgnoreCaseAndStatusIgnoreCase(titleSearch, status, pageable);
        } else if (titleSearch != null && !titleSearch.trim().isEmpty()) {
            return jobRepository.findByTitleContainingIgnoreCase(titleSearch, pageable);
        } else if (status != null && !status.equalsIgnoreCase("ALL")) {
            return jobRepository.findByStatusIgnoreCase(status, pageable);
        }
        return jobRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Job> getJobById(UUID id) {
        return jobRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<JobApplication> getApplicationsForJob(UUID jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }

    @Transactional(readOnly = true)
    public List<JobAssignment> getAssignmentsForJob(UUID jobId) {
        return jobAssignmentRepository.findByJobId(jobId);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public Job updateJobStatus(UUID jobId, String targetStatus, String reason) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        String oldStatus = job.getStatus();
        job.setStatus(targetStatus.toLowerCase());
        Job saved = jobRepository.save(job);

        String diff = String.format("{\"status\": {\"from\": \"%s\", \"to\": \"%s\"}}", oldStatus, targetStatus);
        auditLogService.recordAudit("JOB_STATUS_CHANGE", "JOB", jobId.toString(), reason, diff);

        return saved;
    }
}
