package com.udyogasethu.admin.service;

import com.udyogasethu.admin.domain.KycRecord;
import com.udyogasethu.admin.repository.KycRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final KycRecordRepository kycRecordRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Page<KycRecord> getKycRecords(String status, Pageable pageable) {
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            return kycRecordRepository.findByStatusIgnoreCase(status, pageable);
        }
        return kycRecordRepository.findAll(pageable);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public KycRecord reviewKyc(UUID kycId, boolean approve, String reason) {
        KycRecord record = kycRecordRepository.findById(kycId)
                .orElseThrow(() -> new IllegalArgumentException("KYC Record not found: " + kycId));

        String oldStatus = record.getStatus();
        String newStatus = approve ? "verified" : "rejected";
        record.setStatus(newStatus);
        if (!approve) {
            record.setRejectionReason(reason);
        }

        KycRecord saved = kycRecordRepository.save(record);
        String action = approve ? "KYC_APPROVED" : "KYC_REJECTED";
        String diff = String.format("{\"status\": {\"from\": \"%s\", \"to\": \"%s\"}}", oldStatus, newStatus);
        auditLogService.recordAudit(action, "KYC_RECORD", kycId.toString(), reason, diff);

        return saved;
    }
}
