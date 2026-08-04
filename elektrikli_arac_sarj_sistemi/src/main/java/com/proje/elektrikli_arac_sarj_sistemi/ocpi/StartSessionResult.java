package com.proje.elektrikli_arac_sarj_sistemi.ocpi;


public class StartSessionResult {
    private final boolean accepted;
    private final String ocpiSessionId;

    public StartSessionResult(boolean accepted, String ocpiSessionId) {
        this.accepted = accepted;
        this.ocpiSessionId = ocpiSessionId;
    }

    public boolean isAccepted() { return accepted; }
    public String getOcpiSessionId() { return ocpiSessionId; }
}
