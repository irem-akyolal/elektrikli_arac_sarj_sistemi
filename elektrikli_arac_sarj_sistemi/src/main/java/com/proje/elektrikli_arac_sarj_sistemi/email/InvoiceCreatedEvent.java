package com.proje.elektrikli_arac_sarj_sistemi.email;

import java.util.UUID;

public record InvoiceCreatedEvent(UUID invoiceId) {
}