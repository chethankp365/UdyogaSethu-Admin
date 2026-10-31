package com.udyogasethu.admin;

import com.udyogasethu.admin.domain.AdminUser;
import com.udyogasethu.admin.repository.AdminUserRepository;
import com.udyogasethu.admin.service.AdminService;
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
class AdminServiceSecurityTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Test
    @WithMockUser(username = "admin@udyogasethu.org", roles = {"SUPER_ADMIN"})
    @DisplayName("Security Invariant: Cannot deactivate the last remaining SUPER_ADMIN")
    void testCannotDeactivateLastSuperAdmin() {
        AdminUser superAdmin = adminUserRepository.findAll().stream()
                .filter(a -> a.getRole().equalsIgnoreCase("SUPER_ADMIN") && a.isActive())
                .findFirst()
                .orElseThrow();

        // If there's only 1 super admin, deactivation must throw IllegalStateException
        if (adminUserRepository.countByRole("SUPER_ADMIN") == 1) {
            assertThrows(IllegalStateException.class, () -> {
                adminService.toggleAdminStatus(superAdmin.getId(), false, "Testing deactivation safeguard");
            });
        }
    }
}
