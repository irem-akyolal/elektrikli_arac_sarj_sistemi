package com.proje.elektrikli_arac_sarj_sistemi.ocpi;

import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiCdrDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OcpiCdrFetcher {

    private static final Logger log = LoggerFactory.getLogger(OcpiCdrFetcher.class);

    private final OcpiClient ocpiClient;

    public OcpiCdrFetcher(OcpiClient ocpiClient) {
        this.ocpiClient = ocpiClient;
    }

    @Retryable(
            maxRetriesString = "${ocpi.sync.retry.max-retries}",
            delayString = "${ocpi.sync.retry.delay}",
            multiplierString = "${ocpi.sync.retry.multiplier}",
            maxDelayString = "${ocpi.sync.retry.max-delay}",
            timeoutString = "${ocpi.sync.retry.timeout}"
    )
    public List<OcpiCdrDto> fetchWithRetry() {
        log.info("CPO'dan CDR listesi çekiliyor...");
        return ocpiClient.fetchNewCdrs();
    }
}