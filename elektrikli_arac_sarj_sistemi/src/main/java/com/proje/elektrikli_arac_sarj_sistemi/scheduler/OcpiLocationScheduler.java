package com.proje.elektrikli_arac_sarj_sistemi.scheduler;

import com.proje.elektrikli_arac_sarj_sistemi.service.OcpiLocationSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OcpiLocationScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(OcpiLocationScheduler.class);

    private final OcpiLocationSyncService ocpiLocationSyncService;

    public OcpiLocationScheduler(
            OcpiLocationSyncService ocpiLocationSyncService) {
        this.ocpiLocationSyncService = ocpiLocationSyncService;
    }

    @Scheduled(fixedDelayString = "${ocpi.sync.locations.fixed-delay}")
    public void syncLocations() {

        try {
            log.info("OCPI location synchronization started.");

            ocpiLocationSyncService.syncLocations();

            log.info("OCPI location synchronization completed.");

        } catch (Exception e) {
            log.error("OCPI location synchronization failed.", e);
        }
    }
}