package com.proje.elektrikli_arac_sarj_sistemi.payment;

public class ProvisionAuthorizationResult {

    private final boolean approved;
    private final String providerReferenceId;

    public ProvisionAuthorizationResult(boolean approved, String providerReferenceId) {
        this.approved = approved;
        this.providerReferenceId = providerReferenceId;
    }

    public boolean isApproved() { return approved; }
    public String getProviderReferenceId() { return providerReferenceId; }
}
