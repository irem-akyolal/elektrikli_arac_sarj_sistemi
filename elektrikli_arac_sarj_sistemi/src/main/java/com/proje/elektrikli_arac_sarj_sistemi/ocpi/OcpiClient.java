package com.proje.elektrikli_arac_sarj_sistemi.ocpi;


import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiCdrDto;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiLocationDto;

import java.util.List;

public interface OcpiClient {

    List<OcpiLocationDto> fetchLocations();

    StartSessionResult startSession(String evseUid, String connectorId);

    void stopSession(String ocpiSessionId);

    List<OcpiCdrDto> fetchNewCdrs(); // OCP'den CDR verilerini çekmek için
}