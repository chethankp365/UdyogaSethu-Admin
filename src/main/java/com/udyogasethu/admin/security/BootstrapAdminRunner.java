package com.udyogasethu.admin.security;

import com.udyogasethu.admin.domain.*;
import com.udyogasethu.admin.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapAdminRunner implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final ProfileRepository profileRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ProduceListingRepository produceListingRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final KycRecordRepository kycRecordRepository;
    private final DisputeRepository disputeRepository;
    private final AuditLogRepository auditLogRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${admin.bootstrap.superadmin-email:admin@udyogasethu.org}")
    private String superAdminEmail;

    @Value("${admin.bootstrap.superadmin-name:Platform Administrator}")
    private String superAdminName;

    @Value("${admin.bootstrap.superadmin-password:}")
    private String superAdminPassword;

    @Value("${admin.seed.demo-data:false}")
    private boolean seedDemoData;

    @Override
    @Transactional
    public void run(String... args) {
        bootstrapSuperAdmin();
        if (seedDemoData) {
            bootstrapInitialDataIfEmpty();
        }
    }

    private void bootstrapSuperAdmin() {
        String email = superAdminEmail.toLowerCase().trim();
        AdminUser superAdmin = adminUserRepository.findByEmailIgnoreCase(email).orElse(null);

        boolean isNew = (superAdmin == null);
        boolean needsPassword = isNew || (superAdmin.getPasswordHash() == null || superAdmin.getPasswordHash().isBlank());

        if (needsPassword) {
            String initialPassword;
            boolean generated = false;

            if (superAdminPassword != null && !superAdminPassword.isBlank()) {
                initialPassword = superAdminPassword;
            } else {
                initialPassword = generateSecureRandomPassword();
                generated = true;
            }

            String passwordHash = passwordEncoder.encode(initialPassword);

            if (isNew) {
                superAdmin = AdminUser.builder()
                        .email(email)
                        .fullName(superAdminName)
                        .role("SUPER_ADMIN")
                        .active(true)
                        .passwordHash(passwordHash)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                adminUserRepository.save(superAdmin);
                log.info("Initialized default Super Admin account: {}", email);

                AuditLog initLog = AuditLog.builder()
                        .adminEmail("system@udyogasethu.org")
                        .action("SYSTEM_BOOTSTRAP")
                        .targetType("ADMIN_USER")
                        .targetId(superAdmin.getId().toString())
                        .reason("Initial Super Admin bootstrap with BCrypt password credentials")
                        .createdAt(Instant.now())
                        .build();
                auditLogRepository.save(initLog);
            } else {
                superAdmin.setPasswordHash(passwordHash);
                superAdmin.setUpdatedAt(Instant.now());
                adminUserRepository.save(superAdmin);
                log.info("Provisioned BCrypt password hash for existing Super Admin account: {}", email);
            }

            if (generated) {
                log.warn("""
                        ================================================================================
                        [SECURITY NOTICE] Auto-generated temporary Super Admin password for '{}':
                        Password: {}
                        Please log in and reset this password immediately via Admin Management.
                        ================================================================================
                        """, email, initialPassword);
            }
        }
    }

    private String generateSecureRandomPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%&*";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder();
        sb.append("Setu!"); // Guarantees symbol and uppercase
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void bootstrapInitialDataIfEmpty() {
        if (profileRepository.count() == 0) {
            log.info("Local environment detected with empty database. Seeding verified realistic UdyogaSethu dataset...");

            // 1. Worker Profile
            UUID workerId = UUID.randomUUID();
            Profile worker = Profile.builder()
                    .id(workerId)
                    .phone("+919876543210")
                    .fullName("Ramesh Kumar")
                    .language("kn")
                    .activeRole("seeker")
                    .isSuspended(false)
                    .dailyEarningsGoalPaise(80000L)
                    .createdAt(Instant.now().minusSeconds(86400 * 30))
                    .build();
            profileRepository.save(worker);

            WorkerProfile wp = WorkerProfile.builder()
                    .userId(workerId)
                    .seekerType("informal")
                    .bio("Experienced agricultural harvester and masonry assistant with 5+ years experience.")
                    .hourlyRatePaise(12000L)
                    .dailyRatePaise(75000L)
                    .hasDrivingLicence(true)
                    .ratingAvg(new BigDecimal("4.85"))
                    .jobsCompletedCount(42)
                    .isVerifiedBadge(true)
                    .build();
            workerProfileRepository.save(wp);

            // 2. Provider Profile
            UUID providerId = UUID.randomUUID();
            Profile provider = Profile.builder()
                    .id(providerId)
                    .phone("+919845012345")
                    .fullName("Anand Rao")
                    .language("en")
                    .activeRole("provider")
                    .isSuspended(false)
                    .createdAt(Instant.now().minusSeconds(86400 * 60))
                    .build();
            profileRepository.save(provider);

            ProviderProfile pp = ProviderProfile.builder()
                    .userId(providerId)
                    .providerType("employer")
                    .typicalCrewSize(6)
                    .isVerifiedBadge(true)
                    .build();
            providerProfileRepository.save(pp);

            // 3. Farmer Profile
            UUID farmerId = UUID.randomUUID();
            Profile farmer = Profile.builder()
                    .id(farmerId)
                    .phone("+919741234567")
                    .fullName("Basavaraj Patil")
                    .language("kn")
                    .activeRole("farmer")
                    .isSuspended(false)
                    .createdAt(Instant.now().minusSeconds(86400 * 90))
                    .build();
            profileRepository.save(farmer);

            FarmerProfile fp = FarmerProfile.builder()
                    .userId(farmerId)
                    .farmName("Patil Organic Farms")
                    .clusterName("Mandya Sugarcane & Paddy Cluster")
                    .totalLandAcres(new BigDecimal("12.5"))
                    .deliveryRadiusKm(new BigDecimal("35.0"))
                    .pickupAvailable(true)
                    .organicCertNumber("IND-ORG-2024-8842")
                    .build();
            farmerProfileRepository.save(fp);

            // 4. Job Posting
            Job job = Job.builder()
                    .providerId(providerId)
                    .title("Paddy Harvest & Sacking Assistance")
                    .wageType("daily")
                    .wagePaise(75000L)
                    .slotsTotal(4)
                    .slotsFilled(1)
                    .geofenceRadiusM(150)
                    .status("open")
                    .startAt(Instant.now().plusSeconds(86400 * 2))
                    .build();
            jobRepository.save(job);

            JobApplication app = JobApplication.builder()
                    .jobId(job.getId())
                    .workerId(workerId)
                    .status("applied")
                    .note("Available for full 3-day harvest shift. Have own safety boots.")
                    .build();
            jobApplicationRepository.save(app);

            // 5. Produce Listing
            ProduceListing listing = ProduceListing.builder()
                    .farmerId(farmerId)
                    .cropName("Fresh Sona Masoori Organic Paddy")
                    .category("grain")
                    .pricePerUnitPaise(4200L) // 42 Rs / kg
                    .unit("kg")
                    .stockQty(new BigDecimal("1500.00"))
                    .reservedQty(new BigDecimal("200.00"))
                    .harvestDate(Instant.now().minusSeconds(86400 * 5))
                    .farmingMethod("Natural Vedic Organic")
                    .status("active")
                    .build();
            produceListingRepository.save(listing);

            // 6. Order
            Order order = Order.builder()
                    .orderCode("OD-2026-9812")
                    .customerId(providerId)
                    .farmerId(farmerId)
                    .totalPaise(420000L)
                    .platformFeePaise(12000L)
                    .status("delivered")
                    .handoverPin("4819")
                    .build();
            orderRepository.save(order);

            // 7. Payment
            Payment payment = Payment.builder()
                    .payerId(providerId)
                    .amountPaise(420000L)
                    .platformFeePaise(12000L)
                    .protectionFeePaise(5000L)
                    .status("captured")
                    .razorpayOrderId("order_Oq7JgV8L09xyz")
                    .razorpayPaymentId("pay_Pq9KxL2M01abc")
                    .receiptNo("RCP-2026-00481")
                    .build();
            paymentRepository.save(payment);

            // 8. KYC Record
            KycRecord kyc = KycRecord.builder()
                    .userId(workerId)
                    .docType("driving_licence")
                    .docNumberMasked("KA-09-XXXX-4819")
                    .status("verified")
                    .build();
            kycRecordRepository.save(kyc);

            // 9. Dispute
            Dispute dispute = Dispute.builder()
                    .complainantId(providerId)
                    .respondentId(workerId)
                    .category("Attendance Discrepancy")
                    .reason("Worker arrived 45 minutes late due to bus breakdown, resolved amicably with overtime compensation.")
                    .status("resolved")
                    .resolutionNotes("Both parties confirmed attendance adjustments in mutual agreement.")
                    .refundAmountPaise(0L)
                    .build();
            disputeRepository.save(dispute);

            log.info("Verified seed dataset generated successfully.");
        }
    }
}
