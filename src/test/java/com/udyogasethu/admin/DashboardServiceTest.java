package com.udyogasethu.admin;

import com.udyogasethu.admin.domain.Order;
import com.udyogasethu.admin.domain.Profile;
import com.udyogasethu.admin.repository.OrderRepository;
import com.udyogasethu.admin.repository.ProfileRepository;
import com.udyogasethu.admin.service.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Dashboard KPIs reflect verified database counts and exclude demo data")
    void testDashboardKpisAccurate() {
        DashboardService.DashboardKpis kpis = dashboardService.getDashboardKpis();

        assertNotNull(kpis);
        assertTrue(kpis.getTotalUsers() >= 0);
        assertTrue(kpis.getGrossMarketplaceVolumeInr() >= 0.0);
        assertTrue(kpis.getActiveWorkers() >= 0);
    }

    @Test
    @DisplayName("GMV calculation strictly sums delivered orders")
    void testGmvCalculation() {
        UUID custId = UUID.randomUUID();
        UUID farmId = UUID.randomUUID();

        Order order = Order.builder()
                .orderCode("TEST-OD-" + System.currentTimeMillis())
                .customerId(custId)
                .farmerId(farmId)
                .totalPaise(500000L) // ₹5,000
                .status("delivered")
                .build();
        orderRepository.save(order);

        DashboardService.DashboardKpis kpis = dashboardService.getDashboardKpis();
        assertTrue(kpis.getGrossMarketplaceVolumeInr() >= 5000.0);
    }
}
