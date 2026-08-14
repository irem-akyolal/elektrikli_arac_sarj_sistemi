package com.proje.elektrikli_arac_sarj_sistemi.payment;

public class PaymentCardInfo {

    private final String cardHolderName;
    private final String cardNumber;
    private final String expireMonth;
    private final String expireYear;
    private final String cvc;

    public PaymentCardInfo(
            String cardHolderName,
            String cardNumber,
            String expireMonth,
            String expireYear,
            String cvc) {

        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.expireMonth = expireMonth;
        this.expireYear = expireYear;
        this.cvc = cvc;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpireMonth() {
        return expireMonth;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public String getCvc() {
        return cvc;
    }
}