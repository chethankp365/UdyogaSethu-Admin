package com.udyogasethu.admin.service;

import com.udyogasethu.admin.domain.AdminUser;
import com.udyogasethu.admin.repository.AdminUserRepository;
import com.udyogasethu.admin.util.InputSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final Set<String> PERMITTED_ROLES = Set.of("SUPER_ADMIN", "ADMIN", "MODERATOR", "SUPPORT");

    private final AdminUserRepository adminUserRepository;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<AdminUser> getAdminUsers(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            String sanitized = InputSanitizer.sanitizeText(search, 100);
            return adminUserRepository.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(sanitized, sanitized, pageable);
        }
        return adminUserRepository.findAll(pageable);
    }

    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public AdminUser createAdmin(String email, String fullName, String role, String rawPassword) {
        if (!InputSanitizer.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid corporate email address format.");
        }
        String cleanEmail = email.toLowerCase().trim();
        if (adminUserRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new IllegalArgumentException("Admin with email " + cleanEmail + " already exists.");
        }

        String cleanName = InputSanitizer.sanitizeText(fullName, 100);
        if (cleanName.isBlank()) {
            throw new IllegalArgumentException("Admin full name cannot be blank.");
        }

        String cleanRole = role.toUpperCase().trim();
        if (!PERMITTED_ROLES.contains(cleanRole)) {
            throw new IllegalArgumentException("Invalid administrative role: " + cleanRole);
        }

        if (!InputSanitizer.isStrongPassword(rawPassword)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long, containing at least one uppercase letter and one digit.");
        }

        AdminUser admin = AdminUser.builder()
                .email(cleanEmail)
                .fullName(cleanName)
                .role(cleanRole)
                .active(true)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        AdminUser saved = adminUserRepository.save(admin);
        auditLogService.recordAudit("CREATE_ADMIN", "ADMIN_USER", saved.getId().toString(),
                "Role: " + cleanRole, null);

        return saved;
    }

    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public AdminUser createAdmin(String email, String fullName, String role) {
        return createAdmin(email, fullName, role, "SetuAdmin2026!");
    }

    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void resetPassword(UUID adminId, String newPassword) {
        if (!InputSanitizer.isStrongPassword(newPassword)) {
            throw new IllegalArgumentException("New password must be at least 8 characters long, containing at least one uppercase letter and one digit.");
        }

        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found: " + adminId));

        admin.setPasswordHash(passwordEncoder.encode(newPassword));
        admin.setUpdatedAt(Instant.now());
        adminUserRepository.save(admin);

        auditLogService.recordAudit("RESET_ADMIN_PASSWORD", "ADMIN_USER", adminId.toString(),
                "Password reset by Super Admin", null);
    }

    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public AdminUser toggleAdminStatus(UUID adminId, boolean active, String reason) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found: " + adminId));

        if (!active && admin.getRole().equalsIgnoreCase("SUPER_ADMIN")) {
            long superAdminCount = adminUserRepository.countByRole("SUPER_ADMIN");
            if (superAdminCount <= 1) {
                throw new IllegalStateException("Security Invariant: Cannot deactivate the last remaining SUPER_ADMIN.");
            }
        }

        boolean prev = admin.isActive();
        admin.setActive(active);
        admin.setUpdatedAt(Instant.now());
        AdminUser saved = adminUserRepository.save(admin);

        String diff = String.format("{\"active\": {\"from\": %s, \"to\": %s}}", prev, active);
        auditLogService.recordAudit("ADMIN_STATUS_CHANGE", "ADMIN_USER", adminId.toString(),
                InputSanitizer.sanitizeText(reason, 255), diff);

        return saved;
    }
}
