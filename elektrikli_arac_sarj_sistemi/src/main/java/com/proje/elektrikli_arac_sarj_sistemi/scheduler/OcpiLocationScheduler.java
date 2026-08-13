package com.proje.elektrikli_arac_sarj_sistemi.scheduler;

import com.proje.elektrikli_arac_sarj_sistemi.service.OcpiLocationSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class OcpiLocationScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(OcpiLocationScheduler.class);

    private static final long TIMEOUT_SECONDS = 30;

    private final OcpiLocationSyncService ocpiLocationSyncService;
    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public OcpiLocationScheduler(
            OcpiLocationSyncService ocpiLocationSyncService) {

        this.ocpiLocationSyncService = ocpiLocationSyncService;
    }

    @Scheduled(fixedDelayString = "${ocpi.sync.locations.fixed-delay}")
    public void syncLocations() {

        log.info("OCPI location synchronization started.");

        Future<?> future =
                executorService.submit(
                        ocpiLocationSyncService::syncLocations
                );

        try {

            future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            log.info("OCPI location synchronization completed.");

        } catch (TimeoutException e) {

            future.cancel(true);

            log.error(
                    "OCPI location synchronization TIMED OUT after {} seconds.",
                    TIMEOUT_SECONDS
            );

        } catch (Exception e) {

            log.error(
                    "OCPI location synchronization failed.",
                    e
            );
        }
    }
}