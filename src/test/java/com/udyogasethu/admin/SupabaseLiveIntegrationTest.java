package com.udyogasethu.admin;

import com.udyogasethu.admin.domain.*;
import com.udyogasethu.admin.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("prod")
@EnabledIfEnvironmentVariable(named = "SUPABASE_DB_PASSWORD", matches = ".+")
class SupabaseLiveIntegrationTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    @Autowired
    private ProviderProfileRepository providerProfileRepository;

    @Autowired
    private FarmerProfileRepository farmerProfileRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private ProduceListingRepository produceListingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KycRecordRepository kycRecordRepository;

    @Autowired
    private DisputeRepository disputeRepository;

    @Test
    @DisplayName("Verify real Supabase database connection and row retrieval across all core tables")
    void testRealSupabaseDataFlow() {
        // 1. Verify Profiles
        List<Profile> profiles = profileRepository.findAll();
        assertNotNull(profiles);
        assertFalse(profiles.isEmpty(), "Real Supabase profiles table must not be empty");
        System.out.println(">>> Verified Real Profiles Count: " + profiles.size());
        Profile sampleProfile = profiles.get(0);
        assertNotNull(sampleProfile.getId());
        assertNotNull(sampleProfile.getActiveRole());

        // 2. Verify Worker Profiles
        List<WorkerProfile> workers = workerProfileRepository.findAll();
        assertNotNull(workers);
        assertFalse(workers.isEmpty(), "Real Supabase worker_profiles table must not be empty");
        System.out.println(">>> Verified Real Workers Count: " + workers.size());

        // 3. Verify Provider Profiles
        List<ProviderProfile> providers = providerProfileRepository.findAll();
        assertNotNull(providers);
        assertFalse(providers.isEmpty(), "Real Supabase provider_profiles table must not be empty");
        System.out.println(">>> Verified Real Providers Count: " + providers.size());

        // 4. Verify Farmer Profiles
        List<FarmerProfile> farmers = farmerProfileRepository.findAll();
        assertNotNull(farmers);
        assertFalse(farmers.isEmpty(), "Real Supabase farmer_profiles table must not be empty");
        System.out.println(">>> Verified Real Farmers Count: " + farmers.size());

        // 5. Verify Jobs
        List<Job> jobs = jobRepository.findAll();
        assertNotNull(jobs);
        assertFalse(jobs.isEmpty(), "Real Supabase jobs table must not be empty");
        System.out.println(">>> Verified Real Jobs Count: " + jobs.size());
        Job sampleJob = jobs.get(0);
        assertNotNull(sampleJob.getTitle());
        assertNotNull(sampleJob.getStatus());

        // 6. Verify Job Applications
        List<JobApplication> applications = jobApplicationRepository.findAll();
        assertNotNull(applications);
        assertFalse(applications.isEmpty(), "Real Supabase job_applications table must not be empty");
        System.out.println(">>> Verified Real Applications Count: " + applications.size());

        // 7. Verify Produce Listings
        List<ProduceListing> listings = produceListingRepository.findAll();
        assertNotNull(listings);
        assertFalse(listings.isEmpty(), "Real Supabase produce_listings table must not be empty");
        System.out.println(">>> Verified Real Produce Listings Count: " + listings.size());
        ProduceListing sampleProduce = listings.get(0);
        assertNotNull(sampleProduce.getCropName());
        assertNotNull(sampleProduce.getPricePerUnitPaise());

        // 8. Verify Payments
        List<Payment> payments = paymentRepository.findAll();
        assertNotNull(payments);
        assertFalse(payments.isEmpty(), "Real Supabase payments table must not be empty");
        System.out.println(">>> Verified Real Payments Count: " + payments.size());

        // 9. Verify KYC Records
        List<KycRecord> kycRecords = kycRecordRepository.findAll();
        assertNotNull(kycRecords);
        assertFalse(kycRecords.isEmpty(), "Real Supabase kyc_records table must not be empty");
        System.out.println(">>> Verified Real KYC Records Count: " + kycRecords.size());

        // 10. Verify Disputes
        List<Dispute> disputes = disputeRepository.findAll();
        assertNotNull(disputes);
        assertFalse(disputes.isEmpty(), "Real Supabase disputes table must not be empty");
        System.out.println(">>> Verified Real Disputes Count: " + disputes.size());
    }

    @Test
    @DisplayName("Verify Admin update operation on a real Supabase record with rollback")
    @Transactional
    void testAdminUpdateOperation() {
        List<Profile> profiles = profileRepository.findAll();
        assertFalse(profiles.isEmpty());
        Profile profile = profiles.get(0);
        boolean originalStatus = profile.isSuspended();

        profile.setSuspended(!originalStatus);
        profileRepository.saveAndFlush(profile);

        Profile reloaded = profileRepository.findById(profile.getId()).orElseThrow();
        assertEquals(!originalStatus, reloaded.isSuspended());
        System.out.println(">>> Successfully verified Admin update round-trip on Profile ID: " + profile.getId());
    }
}
