package com.udyogasethu.admin.service;

import com.udyogasethu.admin.domain.Dispute;
import com.udyogasethu.admin.repository.DisputeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Page<Dispute> getDisputes(String status, Pageable pageable) {
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            return disputeRepository.findByStatusIgnoreCase(status, pageable);
        }
        return disputeRepository.findAll(pageable);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public Dispute resolveDispute(UUID disputeId, String newStatus, String resolutionNotes, Long refundPaise) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new IllegalArgumentException("Dispute not found: " + disputeId));

        String oldStatus = dispute.getStatus();
        dispute.setStatus(newStatus.toLowerCase());
        dispute.setResolutionNotes(resolutionNotes);
        if (refundPaise != null) {
            dispute.setRefundAmountPaise(refundPaise);
        }

        Dispute saved = disputeRepository.save(dispute);
        String diff = String.format("{\"status\": {\"from\": \"%s\", \"to\": \"%s\"}, \"refundPaise\": %d}",
                oldStatus, newStatus, refundPaise != null ? refundPaise : 0);
        auditLogService.recordAudit("DISPUTE_STATUS_CHANGE", "DISPUTE", disputeId.toString(), resolutionNotes, diff);

        return saved;
    }
}
