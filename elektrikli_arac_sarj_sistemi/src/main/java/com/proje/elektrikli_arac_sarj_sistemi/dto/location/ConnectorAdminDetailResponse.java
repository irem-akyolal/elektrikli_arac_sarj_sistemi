package com.proje.elektrikli_arac_sarj_sistemi.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorAdminDetailResponse {

    private UUID id;

    private String connectorId;

    private String standard;

    private String format;

    private String powerType;

    private Integer maxVoltage;

    private Integer maxAmperage;

    private Integer maxElectricPowerWatt;

    private BigDecimal unitPrice;

    private String status;

    private boolean charging;
}