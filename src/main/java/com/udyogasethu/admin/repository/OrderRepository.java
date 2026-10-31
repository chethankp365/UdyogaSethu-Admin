package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByOrderCodeContainingIgnoreCase(String orderCode, Pageable pageable);
    Page<Order> findByStatusIgnoreCase(String status, Pageable pageable);
    long countByStatusIgnoreCase(String status);

    @Query("SELECT COALESCE(SUM(o.totalPaise), 0) FROM Order o WHERE LOWER(o.status) = 'delivered'")
    Long calculateGrossDeliveredVolumePaise();
}
