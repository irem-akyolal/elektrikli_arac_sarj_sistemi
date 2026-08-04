package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ChargingSessionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ConnectorRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.LocationRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.PaymentRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.admin.DashboardSummaryResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    private final LocationRepository locationRepository;
    private final ConnectorRepository connectorRepository;
    private final ChargingSessionRepository chargingSessionRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(LocationRepository locationRepository,
                             ConnectorRepository connectorRepository,
                             ChargingSessionRepository chargingSessionRepository,
                             PaymentRepository paymentRepository) {
        this.locationRepository = locationRepository;
        this.connectorRepository = connectorRepository;
        this.chargingSessionRepository = chargingSessionRepository;
        this.paymentRepository = paymentRepository;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        long totalLocations = locationRepository.count();
        long activeLocations = locationRepository.countByActiveTrue();
        long totalConnectors = connectorRepository.count();
        long availableConnectors = connectorRepository.countAvailable();
        long activeSessions = chargingSessionRepository.countByStatus(SessionStatus.CHARGING);
        long todayCompletedSessions = chargingSessionRepository.countCompletedSince(startOfDay);

        var todayRevenue = paymentRepository.sumRevenueSince(startOfDay);
        var totalRevenue = paymentRepository.sumTotalRevenue();

        return new DashboardSummaryResponse(
                totalLocations, activeLocations,
                totalConnectors, availableConnectors,
                activeSessions, todayCompletedSessions,
                todayRevenue, totalRevenue
        );
    }
}