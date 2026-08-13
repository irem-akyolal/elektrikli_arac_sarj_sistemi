package com.proje.elektrikli_arac_sarj_sistemi.scheduler;

import com.proje.elektrikli_arac_sarj_sistemi.service.OcpiCdrProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class OcpiCdrScheduler {

    private static final Logger log = LoggerFactory.getLogger(OcpiCdrScheduler.class);
    private static final long TIMEOUT_SECONDS = 30;

    private final OcpiCdrProcessingService ocpiCdrProcessingService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public OcpiCdrScheduler(OcpiCdrProcessingService ocpiCdrProcessingService) {
        this.ocpiCdrProcessingService = ocpiCdrProcessingService;
    }

    @Scheduled(fixedDelayString = "${ocpi.sync.cdr.fixed-delay}")
    public void syncCdrs() {
        log.info("OCPI CDR synchronization started.");

        Future<?> future = executorService.submit(ocpiCdrProcessingService::processNewCdrs);

        try {
            future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            log.info("OCPI CDR synchronization completed.");
        } catch (TimeoutException e) {
            future.cancel(true);
            log.error("OCPI CDR synchronization TIMED OUT after {} seconds.", TIMEOUT_SECONDS);
        } catch (Exception e) {
            log.error("OCPI CDR synchronization failed.", e);
        }
    }
}