package com.proje.elektrikli_arac_sarj_sistemi.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorAvailabilitySummary {
    private String powerType;      // "AC_1_PHASE", "AC_3_PHASE", "DC" gibi
    private long totalCount;
    private long availableCount;
    private BigDecimal unitPrice;  // aynı tip için genelde sabit fiyat, ortalama alıyoruz
}
