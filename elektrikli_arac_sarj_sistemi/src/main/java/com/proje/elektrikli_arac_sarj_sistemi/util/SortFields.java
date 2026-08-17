package com.proje.elektrikli_arac_sarj_sistemi.util;

import java.util.Set;

public final class SortFields {

    private SortFields() {
    }

    public static final Set<String> PUBLIC_LOCATION = Set.of(
            "name",
            "city",
            "createdAt"
    );

    public static final Set<String> ADMIN_LOCATION = Set.of(
            "name",
            "city",
            "createdAt",
            "updatedAt",
            "active"
    );

    // EVSE ADMIN
    public static final Set<String> ADMIN_EVSE = Set.of(
            "evseId",
            "status",
            "createdAt",
            "updatedAt"
    );

    // CONNECTOR ADMIN
    public static final Set<String> ADMIN_CONNECTOR = Set.of(
            "standard",
            "powerType",
            "unitPrice",
            "createdAt",
            "updatedAt"
    );

    // PROVISION ADMIN
    public static final Set<String> ADMIN_PROVISION = Set.of(
            "status",
            "createdAt",
            "updatedAt"
    );

    // PAYMENT ADMIN
    public static final Set<String> ADMIN_PAYMENT = Set.of(
            "status",
            "amount",
            "refundAmount",
            "createdAt",
            "updatedAt"
    );

    // CHARGING SESSION ADMIN
    public static final Set<String> ADMIN_CHARGING_SESSION = Set.of(
            "status",
            "startedAt",
            "completedAt",
            "createdAt"
    );


    public static final Set<String> ADMIN_EMAIL_QUEUE =
        Set.of(
                "createdAt",
                "sentAt",
                "status",
                "attemptCount"
        );
}