package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Page<Payment> findByReceiptNoContainingIgnoreCaseOrRazorpayOrderIdContainingIgnoreCase(
            String receiptNo, String orderId, Pageable pageable);
    Page<Payment> findByStatusIgnoreCase(String status, Pageable pageable);
    long countByStatusIgnoreCase(String status);
}
