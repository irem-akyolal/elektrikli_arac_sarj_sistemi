package com.proje.elektrikli_arac_sarj_sistemi.Repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface ConnectorAvailabilityProjection {
    UUID getLocationId();
    String getPowerType();
    Long getTotalCount();
    Long getAvailableCount();
    BigDecimal getUnitPrice();
}