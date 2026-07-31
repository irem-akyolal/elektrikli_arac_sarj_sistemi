package com.proje.elektrikli_arac_sarj_sistemi.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorSummary {
    private String connectorId;
    private String standard;        //  CCS, CHAdeMO, Type2
    private String status;          // AVAILABLE, CHARGING (EVSE üzerinden)
    private String powerType;       //  AC_1_PHASE, DC
    private Integer maxPowerWatt;   // Maksimum güç (Watt)
    private BigDecimal unitPrice;   // kWh başına fiyat
}