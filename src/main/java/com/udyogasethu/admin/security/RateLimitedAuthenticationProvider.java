package com.udyogasethu.admin.security;

import com.udyogasethu.admin.domain.AdminUser;
import com.udyogasethu.admin.repository.AdminUserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

/**
 * Enterprise Authentication Provider enforcing BCrypt password hashing,
 * account lockout rate-limiting, and last login telemetry tracking.
 */
public class RateLimitedAuthenticationProvider extends DaoAuthenticationProvider {

    private final LoginRateLimiter rateLimiter;
    private final AdminUserRepository adminUserRepository;

    public RateLimitedAuthenticationProvider(AdminUserDetailsService userDetailsService,
                                            PasswordEncoder passwordEncoder,
                                            LoginRateLimiter rateLimiter,
                                            AdminUserRepository adminUserRepository) {
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
        this.rateLimiter = rateLimiter;
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        if (rateLimiter.isBlocked(username)) {
            throw new LockedException("Account temporarily locked due to multiple failed login attempts. Try again in 15 minutes.");
        }

        try {
            Authentication auth = super.authenticate(authentication);
            rateLimiter.recordSuccess(username);

            // Record last login timestamp asynchronously / transactionally
            adminUserRepository.findByEmailIgnoreCase(username).ifPresent(user -> {
                user.setLastLoginAt(Instant.now());
                adminUserRepository.save(user);
            });

            return auth;
        } catch (BadCredentialsException ex) {
            rateLimiter.recordFailure(username);
            int remaining = rateLimiter.getRemainingAttempts(username);
            if (remaining <= 0) {
                throw new LockedException("Account temporarily locked due to multiple failed login attempts. Try again in 15 minutes.");
            }
            throw new BadCredentialsException("Invalid admin credentials. " + remaining + " attempt(s) remaining before temporary lockout.");
        } catch (AuthenticationException ex) {
            rateLimiter.recordFailure(username);
            throw ex;
        }
    }
}
