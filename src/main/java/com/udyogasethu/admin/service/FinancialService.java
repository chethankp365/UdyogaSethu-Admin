package com.udyogasethu.admin.service;

import com.udyogasethu.admin.domain.DemoTransaction;
import com.udyogasethu.admin.domain.LedgerEntry;
import com.udyogasethu.admin.domain.Payment;
import com.udyogasethu.admin.domain.Payout;
import com.udyogasethu.admin.repository.DemoTransactionRepository;
import com.udyogasethu.admin.repository.LedgerEntryRepository;
import com.udyogasethu.admin.repository.PaymentRepository;
import com.udyogasethu.admin.repository.PayoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialService {

    private final PaymentRepository paymentRepository;
    private final PayoutRepository payoutRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final DemoTransactionRepository demoTransactionRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Page<Payment> getPayments(String search, String status, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return paymentRepository.findByReceiptNoContainingIgnoreCaseOrRazorpayOrderIdContainingIgnoreCase(search, search, pageable);
        }
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            return paymentRepository.findByStatusIgnoreCase(status, pageable);
        }
        return paymentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Payout> getPayouts(String status, Pageable pageable) {
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            return payoutRepository.findByStatusIgnoreCase(status, pageable);
        }
        return payoutRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<LedgerEntry> getLedgerEntries(Pageable pageable) {
        return ledgerEntryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<DemoTransaction> getDemoTransactions(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return demoTransactionRepository.findByScenarioNameContainingIgnoreCaseOrTransactionCodeContainingIgnoreCase(
                    search, search, pageable);
        }
        return demoTransactionRepository.findAll(pageable);
    }

    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public DemoTransaction createDemoTransaction(String scenario, Long amountPaise, String metadata) {
        DemoTransaction demo = DemoTransaction.builder()
                .transactionCode("DEMO-TX-" + System.currentTimeMillis())
                .scenarioName(scenario)
                .amountPaise(amountPaise)
                .status("completed")
                .metadata(metadata)
                .createdAt(Instant.now())
                .build();

        DemoTransaction saved = demoTransactionRepository.save(demo);
        auditLogService.recordAudit("CREATE_DEMO_TRANSACTION", "DEMO_TRANSACTION", saved.getId().toString(),
                "Sandbox simulation: " + scenario, null);
        return saved;
    }

    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void clearAllDemoTransactions(String reason) {
        long count = demoTransactionRepository.count();
        demoTransactionRepository.deleteAll();
        auditLogService.recordAudit("CLEAR_DEMO_TRANSACTIONS", "DEMO_TRANSACTION", "ALL",
                reason, String.format("{\"clearedCount\": %d}", count));
    }
}
