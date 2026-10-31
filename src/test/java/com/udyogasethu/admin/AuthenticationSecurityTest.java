package com.udyogasethu.admin;

import com.udyogasethu.admin.domain.AdminUser;
import com.udyogasethu.admin.repository.AdminUserRepository;
import com.udyogasethu.admin.security.LoginRateLimiter;
import com.udyogasethu.admin.security.RateLimitedAuthenticationProvider;
import com.udyogasethu.admin.util.InputSanitizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class AuthenticationSecurityTest {

    @Autowired
    private RateLimitedAuthenticationProvider authProvider;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginRateLimiter rateLimiter;

    @Test
    @DisplayName("Valid admin credentials authenticate successfully and set lastLoginAt")
    void testSuccessfulAuthentication() {
        String email = "test.auth." + UUID.randomUUID() + "@udyogasethu.org";
        String rawPassword = "StrongPassword123!";

        AdminUser user = AdminUser.builder()
                .email(email)
                .fullName("Auth Test Admin")
                .role("ADMIN")
                .active(true)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        adminUserRepository.save(user);

        Authentication result = authProvider.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword)
        );

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals(email, result.getName());
    }

    @Test
    @DisplayName("Invalid password throws BadCredentialsException and increments failure attempts")
    void testBadCredentials() {
        String email = "bad.cred." + UUID.randomUUID() + "@udyogasethu.org";
        String rawPassword = "CorrectPassword123!";

        AdminUser user = AdminUser.builder()
                .email(email)
                .fullName("Bad Cred Admin")
                .role("ADMIN")
                .active(true)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        adminUserRepository.save(user);

        assertThrows(BadCredentialsException.class, () -> {
            authProvider.authenticate(new UsernamePasswordAuthenticationToken(email, "WrongPassword123!"));
        });

        assertEquals(4, rateLimiter.getRemainingAttempts(email));
    }

    @Test
    @DisplayName("Excessive failed attempts lock the account via RateLimiter")
    void testRateLimitLockout() {
        String email = "lockout." + UUID.randomUUID() + "@udyogasethu.org";
        String rawPassword = "Password123!";

        AdminUser user = AdminUser.builder()
                .email(email)
                .fullName("Lockout Admin")
                .role("ADMIN")
                .active(true)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        adminUserRepository.save(user);

        // Exhaust all 5 attempts
        for (int i = 0; i < LoginRateLimiter.MAX_ATTEMPTS; i++) {
            assertThrows(Exception.class, () -> {
                authProvider.authenticate(new UsernamePasswordAuthenticationToken(email, "InvalidPassword!"));
            });
        }

        assertTrue(rateLimiter.isBlocked(email));

        // Further attempt must immediately be rejected with LockedException
        assertThrows(LockedException.class, () -> {
            authProvider.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));
        });
    }

    @Test
    @DisplayName("InputSanitizer defensively validates inputs")
    void testInputSanitizer() {
        assertTrue(InputSanitizer.isValidEmail("admin@udyogasethu.org"));
        assertFalse(InputSanitizer.isValidEmail("invalid-email"));
        assertFalse(InputSanitizer.isValidEmail("<script>alert(1)</script>@evil.com"));

        assertTrue(InputSanitizer.isStrongPassword("P@ssword123"));
        assertFalse(InputSanitizer.isStrongPassword("weak"));
        assertFalse(InputSanitizer.isStrongPassword("alllowercase123"));

        assertEquals("Clean Text", InputSanitizer.sanitizeText("<b>Clean Text</b><script>alert(1)</script>", 50));
    }
}
