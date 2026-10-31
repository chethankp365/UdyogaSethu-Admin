package com.udyogasethu.admin.repository;

import com.udyogasethu.admin.domain.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Page<Profile> findByFullNameContainingIgnoreCaseOrPhoneContainingIgnoreCase(String name, String phone, Pageable pageable);
    Page<Profile> findByActiveRoleIgnoreCase(String activeRole, Pageable pageable);
    long countByActiveRoleIgnoreCase(String activeRole);
    long countByActiveRoleIgnoreCaseAndIsSuspendedFalse(String activeRole);
    long countByIsSuspended(boolean suspended);
}
