package com.proje.elektrikli_arac_sarj_sistemi.ocpi;

import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiLocationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OcpiLocationFetcher {

    private static final Logger log =
            LoggerFactory.getLogger(OcpiLocationFetcher.class);

    private final OcpiClient ocpiClient;

    public OcpiLocationFetcher(OcpiClient ocpiClient) {
        this.ocpiClient = ocpiClient;
    }

    @Retryable(
            maxRetriesString = "${ocpi.sync.retry.max-retries}",
            delayString = "${ocpi.sync.retry.delay}",
            multiplierString = "${ocpi.sync.retry.multiplier}",
            maxDelayString = "${ocpi.sync.retry.max-delay}",
            timeoutString = "${ocpi.sync.retry.timeout}"
    )
    public List<OcpiLocationDto> fetchWithRetry() {

        log.info("CPO'dan location listesi çekiliyor...");

        return ocpiClient.fetchLocations();
    }
}