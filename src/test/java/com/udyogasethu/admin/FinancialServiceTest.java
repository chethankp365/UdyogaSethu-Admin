package com.udyogasethu.admin;

import com.udyogasethu.admin.domain.DemoTransaction;
import com.udyogasethu.admin.repository.DemoTransactionRepository;
import com.udyogasethu.admin.repository.OrderRepository;
import com.udyogasethu.admin.service.FinancialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class FinancialServiceTest {

    @Autowired
    private FinancialService financialService;

    @Autowired
    private DemoTransactionRepository demoTransactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @WithMockUser(username = "admin@udyogasethu.org", roles = {"SUPER_ADMIN"})
    @DisplayName("Demo transaction creation is isolated in admin schema and never affects real gross order volume")
    void testDemoIsolation() {
        Long initialRealVolume = orderRepository.calculateGrossDeliveredVolumePaise();
        long initialDemoCount = demoTransactionRepository.count();

        DemoTransaction demo = financialService.createDemoTransaction(
                "High Volume Escrow Simulation",
                100000000L, // ₹1,000,000 simulated
                "{\"simulated\": true}"
        );

        assertNotNull(demo.getId());
        assertEquals(initialDemoCount + 1, demoTransactionRepository.count());

        // Verify real order totals are completely unaffected
        Long postDemoVolume = orderRepository.calculateGrossDeliveredVolumePaise();
        assertEquals(initialRealVolume, postDemoVolume);

        // Verify purge clears demo rows only
        financialService.clearAllDemoTransactions("Test cleanup");
        assertEquals(0, demoTransactionRepository.count());
    }
}
