package com.proje.elektrikli_arac_sarj_sistemi.scheduler;

import com.proje.elektrikli_arac_sarj_sistemi.service.OcpiCdrProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OcpiCdrScheduler {

    private static final Logger log = LoggerFactory.getLogger(OcpiCdrScheduler.class);

    private final OcpiCdrProcessingService ocpiCdrProcessingService;

    public OcpiCdrScheduler(OcpiCdrProcessingService ocpiCdrProcessingService) {
        this.ocpiCdrProcessingService = ocpiCdrProcessingService;
    }

    @Scheduled(fixedDelayString = "${ocpi.sync.cdr.fixed-delay}")
    public void syncCdrs() {
        try {
            log.info("OCPI CDR synchronization started.");
            ocpiCdrProcessingService.processNewCdrs();
            log.info("OCPI CDR synchronization completed.");
        } catch (Exception e) {
            log.error("OCPI CDR synchronization failed.", e);
        }
    }
}
