package com.udyogasethu.admin.service;

import com.udyogasethu.admin.repository.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProfileRepository profileRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final JobRepository jobRepository;
    private final ProduceListingRepository produceListingRepository;
    private final OrderRepository orderRepository;
    private final KycRecordRepository kycRecordRepository;
    private final DisputeRepository disputeRepository;

    @Getter
    @Builder
    public static class DashboardKpis {
        private long totalUsers;
        private long activeWorkers;
        private long serviceProviders;
        private long farmers;
        private long openJobs;
        private long filledJobs;
        private long activeListings;
        private long completedOrders;
        private double grossMarketplaceVolumeInr;
        private long pendingVerifications;
        private long openDisputes;
    }

    @Transactional(readOnly = true)
    public DashboardKpis getDashboardKpis() {
        Long gmvPaise = orderRepository.calculateGrossDeliveredVolumePaise();
        double gmvInr = (gmvPaise != null ? gmvPaise : 0L) / 100.0;

        return DashboardKpis.builder()
                .totalUsers(profileRepository.count())
                .activeWorkers(profileRepository.countByActiveRoleIgnoreCaseAndIsSuspendedFalse("seeker"))
                .serviceProviders(profileRepository.countByActiveRoleIgnoreCaseAndIsSuspendedFalse("provider"))
                .farmers(profileRepository.countByActiveRoleIgnoreCaseAndIsSuspendedFalse("farmer"))
                .openJobs(jobRepository.countByStatusIgnoreCase("open"))
                .filledJobs(jobRepository.countByStatusIgnoreCase("filled"))
                .activeListings(produceListingRepository.countByStatusIgnoreCase("active"))
                .completedOrders(orderRepository.countByStatusIgnoreCase("delivered"))
                .grossMarketplaceVolumeInr(gmvInr)
                .pendingVerifications(kycRecordRepository.countByStatusIgnoreCase("pending"))
                .openDisputes(disputeRepository.countByStatusIgnoreCase("open") + disputeRepository.countByStatusIgnoreCase("under_review"))
                .build();
    }
}
