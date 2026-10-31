package com.udyogasethu.admin.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe rate limiter protecting administrative login and authentication endpoints.
 * Enforces maximum 5 failed attempts per account/key with a 15-minute sliding lockout.
 */
@Component
public class LoginRateLimiter {

    public static final int MAX_ATTEMPTS = 5;
    public static final long LOCKOUT_DURATION_SECONDS = 15 * 60; // 15 minutes

    private static class AttemptRecord {
        int attempts;
        Instant firstAttemptAt;
        Instant lockedUntil;

        AttemptRecord(Instant now) {
            this.attempts = 1;
            this.firstAttemptAt = now;
            this.lockedUntil = null;
        }
    }

    private final Map<String, AttemptRecord> attemptCache = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        if (key == null) return false;
        String normalizedKey = key.trim().toLowerCase();
        AttemptRecord record = attemptCache.get(normalizedKey);
        if (record == null) return false;

        Instant now = Instant.now();
        if (record.lockedUntil != null) {
            if (now.isBefore(record.lockedUntil)) {
                return true;
            } else {
                // Lockout expired, clear record
                attemptCache.remove(normalizedKey);
                return false;
            }
        }
        return false;
    }

    public void recordFailure(String key) {
        if (key == null) return;
        String normalizedKey = key.trim().toLowerCase();
        Instant now = Instant.now();

        attemptCache.compute(normalizedKey, (k, record) -> {
            if (record == null) {
                return new AttemptRecord(now);
            }

            // If existing window exceeded lockout duration without lock, reset
            if (record.lockedUntil == null && now.isAfter(record.firstAttemptAt.plusSeconds(LOCKOUT_DURATION_SECONDS))) {
                return new AttemptRecord(now);
            }

            record.attempts++;
            if (record.attempts >= MAX_ATTEMPTS) {
                record.lockedUntil = now.plusSeconds(LOCKOUT_DURATION_SECONDS);
            }
            return record;
        });
    }

    public void recordSuccess(String key) {
        if (key == null) return;
        attemptCache.remove(key.trim().toLowerCase());
    }

    public int getRemainingAttempts(String key) {
        if (key == null) return MAX_ATTEMPTS;
        AttemptRecord record = attemptCache.get(key.trim().toLowerCase());
        if (record == null) return MAX_ATTEMPTS;
        return Math.max(0, MAX_ATTEMPTS - record.attempts);
    }
}
