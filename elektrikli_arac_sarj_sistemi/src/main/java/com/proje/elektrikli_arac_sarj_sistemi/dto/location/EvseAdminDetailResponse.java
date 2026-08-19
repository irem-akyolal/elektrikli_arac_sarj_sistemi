package com.proje.elektrikli_arac_sarj_sistemi.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvseAdminDetailResponse {

    private UUID id;

    private String ocpiEvseUid;

    private String evseId;

    private String status;

    private List<ConnectorAdminDetailResponse> connectors;
}