package com.udyogasethu.admin.service;

import com.udyogasethu.admin.domain.Order;
import com.udyogasethu.admin.domain.OrderItem;
import com.udyogasethu.admin.domain.ProduceListing;
import com.udyogasethu.admin.repository.OrderItemRepository;
import com.udyogasethu.admin.repository.OrderRepository;
import com.udyogasethu.admin.repository.ProduceListingRepository;
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
public class MarketplaceService {

    private final ProduceListingRepository produceListingRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Page<ProduceListing> getListings(String search, String status, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return produceListingRepository.findByCropNameContainingIgnoreCase(search, pageable);
        }
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            return produceListingRepository.findByStatusIgnoreCase(status, pageable);
        }
        return produceListingRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrders(String search, String status, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return orderRepository.findByOrderCodeContainingIgnoreCase(search, pageable);
        }
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            return orderRepository.findByStatusIgnoreCase(status, pageable);
        }
        return orderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ProduceListing updateListingStatus(UUID listingId, String targetStatus, String reason) {
        ProduceListing listing = produceListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found: " + listingId));

        String oldStatus = listing.getStatus();
        listing.setStatus(targetStatus.toLowerCase());
        ProduceListing saved = produceListingRepository.save(listing);

        String diff = String.format("{\"status\": {\"from\": \"%s\", \"to\": \"%s\"}}", oldStatus, targetStatus);
        auditLogService.recordAudit("LISTING_STATUS_CHANGE", "PRODUCE_LISTING", listingId.toString(), reason, diff);

        return saved;
    }
}
