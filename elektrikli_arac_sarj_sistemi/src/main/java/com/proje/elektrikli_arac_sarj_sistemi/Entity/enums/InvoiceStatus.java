package com.proje.elektrikli_arac_sarj_sistemi.Entity.enums;

public enum InvoiceStatus {
    CREATED,  // Fatura oluşturuldu
    SENT,     // Kullanıcıya e-posta ile gönderildi
    FAILED    // E-posta gönderimi/PDF üretimi başarısız oldu
}