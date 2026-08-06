package com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class OcpiCdrCostDto {

    @JsonProperty("excl_vat")
    private BigDecimal exclVat;

    @JsonProperty("incl_vat")
    private BigDecimal inclVat;

    public BigDecimal getExclVat() { return exclVat; }
    public void setExclVat(BigDecimal exclVat) { this.exclVat = exclVat; }
    public BigDecimal getInclVat() { return inclVat; }
    public void setInclVat(BigDecimal inclVat) { this.inclVat = inclVat; }
}