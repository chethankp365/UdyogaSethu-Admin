package com.udyogasethu.admin;

import com.udyogasethu.admin.domain.AdminUser;
import com.udyogasethu.admin.domain.Profile;
import com.udyogasethu.admin.repository.AdminUserRepository;
import com.udyogasethu.admin.repository.ProfileRepository;
import com.udyogasethu.admin.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class MethodSecurityAuthorizationTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private FinancialService financialService;

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private DisputeService disputeService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @WithMockUser(username = "admin@udyogasethu.org", roles = {"ADMIN"})
    @DisplayName("ROLE_ADMIN is strictly denied from executing SUPER_ADMIN operations (createAdmin, resetPassword, demoTransactions)")
    void testAdminCannotExecuteSuperAdminOperations() {
        // 1. Attempting to create admin as ROLE_ADMIN must throw AccessDeniedException
        assertThrows(AccessDeniedException.class, () -> {
            adminService.createAdmin("new.admin@udyogasethu.org", "Unauthorized Admin", "ADMIN", "ValidPass123!");
        });

        // 2. Attempting to reset password as ROLE_ADMIN must throw AccessDeniedException
        assertThrows(AccessDeniedException.class, () -> {
            adminService.resetPassword(UUID.randomUUID(), "ValidPass123!");
        });

        // 3. Attempting to toggle admin status as ROLE_ADMIN must throw AccessDeniedException
        assertThrows(AccessDeniedException.class, () -> {
            adminService.toggleAdminStatus(UUID.randomUUID(), false, "Unauthorized attempt");
        });

        // 4. Attempting to create demo financial transaction as ROLE_ADMIN must throw AccessDeniedException
        assertThrows(AccessDeniedException.class, () -> {
            financialService.createDemoTransaction("Unauthorized Simulation", 500000L, "{}");
        });

        // 5. Attempting to clear demo transactions as ROLE_ADMIN must throw AccessDeniedException
        assertThrows(AccessDeniedException.class, () -> {
            financialService.clearAllDemoTransactions("Unauthorized cleanup");
        });
    }

    @Test
    @WithMockUser(username = "normal.user@udyogasethu.org", roles = {"USER"})
    @DisplayName("Normal user (ROLE_USER) is strictly denied from all admin service operations")
    void testNormalUserCannotExecuteAdminOperations() {
        UUID randomId = UUID.randomUUID();

        // 1. Suspension
        assertThrows(AccessDeniedException.class, () -> {
            userService.toggleUserSuspension(randomId, true, "Unauthorized");
        });

        // 2. KYC review
        assertThrows(AccessDeniedException.class, () -> {
            verificationService.reviewKyc(randomId, true, "Unauthorized");
        });

        // 3. Dispute resolution
        assertThrows(AccessDeniedException.class, () -> {
            disputeService.resolveDispute(randomId, "resolved", "Unauthorized", 0L);
        });

        // 4. Notification broadcasting
        assertThrows(AccessDeniedException.class, () -> {
            notificationService.broadcastNotification(randomId, "Test", "Body", "broadcast");
        });

        // 5. Admin management
        assertThrows(AccessDeniedException.class, () -> {
            adminService.createAdmin("hack@udyogasethu.org", "Hacker", "SUPER_ADMIN", "Pass1234!");
        });
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Unauthenticated caller is denied from all state-changing service operations")
    void testUnauthenticatedCallerDenied() {
        UUID randomId = UUID.randomUUID();

        assertThrows(Exception.class, () -> {
            userService.toggleUserSuspension(randomId, true, "Unauthorized");
        });

        assertThrows(Exception.class, () -> {
            adminService.createAdmin("anon@udyogasethu.org", "Anon", "ADMIN", "Pass1234!");
        });
    }

    @Test
    @WithMockUser(username = "superadmin@udyogasethu.org", roles = {"SUPER_ADMIN"})
    @DisplayName("ROLE_SUPER_ADMIN is authorized to provision administrators and perform governance")
    void testSuperAdminAuthorized() {
        String testEmail = "prov.admin." + UUID.randomUUID() + "@udyogasethu.org";
        AdminUser created = adminService.createAdmin(testEmail, "Provisioned Admin", "ADMIN", "StrongPass2026!");
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("ADMIN", created.getRole());
    }

    @Test
    @WithMockUser(username = "admin@udyogasethu.org", roles = {"ADMIN"})
    @DisplayName("ROLE_ADMIN is authorized to perform operational suspension on mobile profiles")
    void testAdminAuthorizedForOperationalSuspension() {
        Profile profile = Profile.builder()
                .id(UUID.randomUUID())
                .fullName("Test Profile")
                .phone("+919999900001")
                .activeRole("seeker")
                .isSuspended(false)
                .build();
        profileRepository.save(profile);

        Profile suspended = userService.toggleUserSuspension(profile.getId(), true, "Operational review");
        assertTrue(suspended.isSuspended());
    }
}
