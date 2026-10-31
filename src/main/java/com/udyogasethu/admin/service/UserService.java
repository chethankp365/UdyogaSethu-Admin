package com.udyogasethu.admin.service;

import com.udyogasethu.admin.domain.*;
import com.udyogasethu.admin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ProfileRepository profileRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Page<Profile> getUsers(String search, String role, Pageable pageable) {
        if (role != null && !role.equalsIgnoreCase("ALL")) {
            return profileRepository.findByActiveRoleIgnoreCase(role, pageable);
        }
        if (search != null && !search.trim().isEmpty()) {
            return profileRepository.findByFullNameContainingIgnoreCaseOrPhoneContainingIgnoreCase(search, search, pageable);
        }
        return profileRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Profile> getUserById(UUID id) {
        return profileRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<WorkerProfile> getWorkerProfile(UUID userId) {
        return workerProfileRepository.findById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<ProviderProfile> getProviderProfile(UUID userId) {
        return providerProfileRepository.findById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<FarmerProfile> getFarmerProfile(UUID userId) {
        return farmerProfileRepository.findById(userId);
    }

    @Transactional(readOnly = true)
    public Page<WorkerProfile> getWorkerProfiles(Pageable pageable) {
        return workerProfileRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProviderProfile> getProviderProfiles(Pageable pageable) {
        return providerProfileRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<FarmerProfile> getFarmerProfiles(Pageable pageable) {
        return farmerProfileRepository.findAll(pageable);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public Profile toggleUserSuspension(UUID userId, boolean suspend, String reason) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        boolean previousState = profile.isSuspended();
        profile.setSuspended(suspend);
        Profile updated = profileRepository.save(profile);

        String action = suspend ? "USER_SUSPENDED" : "USER_RESTORED";
        String diff = String.format("{\"isSuspended\": {\"from\": %s, \"to\": %s}}", previousState, suspend);
        auditLogService.recordAudit(action, "PROFILE", userId.toString(), reason, diff);

        return updated;
    }
}
