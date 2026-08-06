package com.proje.elektrikli_arac_sarj_sistemi.ocpi;

import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiLocationDto;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Profile("cpo")
public class OcpiHttpClient implements OcpiClient {

    private final RestClient restClient;

    public OcpiHttpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<OcpiLocationDto> fetchLocations() {

        return restClient.get()
                .uri("/locations")
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<OcpiLocationDto>>() {});
    }

    @Override
    public StartSessionResult startSession(
            String evseUid,
            String connectorId) {

        throw new UnsupportedOperationException(
                "HTTP startSession is not implemented yet."
        );
    }

    @Override
    public void stopSession(String ocpiSessionId) {

        throw new UnsupportedOperationException(
                "HTTP stopSession is not implemented yet."
        );
    }
}