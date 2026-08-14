package com.proje.elektrikli_arac_sarj_sistemi.dto.provision;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class ProvisionCreateRequest {

    @NotNull(message = "Şarj oturumu ID boş olamaz")
    private UUID chargingSessionId;

    @NotNull(message = "Talep edilen tutar boş olamaz")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Tutar 0'dan büyük olmalı"
    )
    private BigDecimal requestedAmount;

    @NotBlank(message = "Kart sahibi adı boş olamaz")
    private String cardHolderName;

    @NotBlank(message = "Kart numarası boş olamaz")
    private String cardNumber;

    @NotBlank(message = "Son kullanma ayı boş olamaz")
    private String expireMonth;

    @NotBlank(message = "Son kullanma yılı boş olamaz")
    private String expireYear;

    @NotBlank(message = "CVV boş olamaz")
    private String cvc;

    public UUID getChargingSessionId() {
        return chargingSessionId;
    }

    public void setChargingSessionId(UUID chargingSessionId) {
        this.chargingSessionId = chargingSessionId;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth) {
        this.expireMonth = expireMonth;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(String expireYear) {
        this.expireYear = expireYear;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
}