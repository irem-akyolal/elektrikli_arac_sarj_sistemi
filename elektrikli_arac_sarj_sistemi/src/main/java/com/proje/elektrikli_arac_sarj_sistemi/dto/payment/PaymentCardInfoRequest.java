package com.proje.elektrikli_arac_sarj_sistemi.dto.payment;

import jakarta.validation.constraints.NotBlank;

public class PaymentCardInfoRequest {

    @NotBlank
    private String cardHolderName;

    @NotBlank
    private String cardNumber;

    @NotBlank
    private String expireMonth;

    @NotBlank
    private String expireYear;

    @NotBlank
    private String cvc;

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