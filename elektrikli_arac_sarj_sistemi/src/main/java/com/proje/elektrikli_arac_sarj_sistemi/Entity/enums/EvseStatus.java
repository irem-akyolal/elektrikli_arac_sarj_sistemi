package com.proje.elektrikli_arac_sarj_sistemi.Entity.enums;

public enum EvseStatus {
    AVAILABLE,
    BLOCKED,
    CHARGING,
    INOPERATIVE,
    OUT_OF_ORDER,      // OCPI'de "OUTOFORDER" şeklinde geçiyor, kendi iş mantığımızda "OUT_OF_ORDER" olarak kullanıyoruz
    PENDING_REMOVAL,   // kendi iş mantığımızda session için kullanıyoruz, OCPI'de yok
    PLANNED,
    REMOVED,
    RESERVED,
    UNKNOWN
}