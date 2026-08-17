package com.proje.elektrikli_arac_sarj_sistemi.Entity.enums;

public enum PaymentStatus {
    AUTHORIZED, // provizyon alındı 
    CAPTURED,  // tahsilat başarılı
    PARTIALLY_REFUNDED, // tahsilat yapıldı, bir kısmı iade edildi
    REFUNDED,  // tutar tamamen iade edildi
    FAILED,   // ödeme tahsilat başarısız oldu
    REFUND_FAILED   // tahsilat başarılı fakat iade başarısız
}