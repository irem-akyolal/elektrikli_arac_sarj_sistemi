package com.proje.elektrikli_arac_sarj_sistemi.dto.admin;

import java.math.BigDecimal;

public class DashboardSummaryResponse {

    private long totalLocations;
    private long activeLocations;
    private long totalConnectors;
    private long availableConnectors;
    private long activeSessions; // şu an CHARGING durumunda olanlar
    private long todayCompletedSessions;
    private BigDecimal todayRevenue;
    private BigDecimal totalRevenue;
    private long unknownConnectorDataCount; // format/standard/powerType herhangi biri UNKNOWN olan connector sayısı

    public DashboardSummaryResponse(long totalLocations, long activeLocations,
                                     long totalConnectors, long availableConnectors,
                                     long activeSessions, long todayCompletedSessions,
                                     BigDecimal todayRevenue, BigDecimal totalRevenue, long unknownConnectorDataCount) {
        this.totalLocations = totalLocations;
        this.activeLocations = activeLocations;
        this.totalConnectors = totalConnectors;
        this.availableConnectors = availableConnectors;
        this.activeSessions = activeSessions;
        this.todayCompletedSessions = todayCompletedSessions;
        this.todayRevenue = todayRevenue;
        this.totalRevenue = totalRevenue;
        this.unknownConnectorDataCount = unknownConnectorDataCount;
    }

    public long getTotalLocations() { return totalLocations; }
    public long getActiveLocations() { return activeLocations; }
    public long getTotalConnectors() { return totalConnectors; }
    public long getAvailableConnectors() { return availableConnectors; }
    public long getActiveSessions() { return activeSessions; }
    public long getTodayCompletedSessions() { return todayCompletedSessions; }
    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public long getUnknownConnectorDataCount() { return unknownConnectorDataCount; }
}
