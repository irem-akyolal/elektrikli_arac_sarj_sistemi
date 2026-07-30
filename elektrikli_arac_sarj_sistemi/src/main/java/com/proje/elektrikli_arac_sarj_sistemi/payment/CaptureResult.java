package com.proje.elektrikli_arac_sarj_sistemi.payment;

public class CaptureResult {

    private final boolean success;
    private final String transactionId;

    public CaptureResult(boolean success, String transactionId) {
        this.success = success;
        this.transactionId = transactionId;
    }

    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
}