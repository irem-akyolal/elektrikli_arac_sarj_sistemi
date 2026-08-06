package com.proje.elektrikli_arac_sarj_sistemi.exception;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
} // limit aşıldığında fırlatılacak özel bir istisna sınıfıdır. Bu sınıf, Rate Limit Exceeded hatalarını temsil eder ve RuntimeException sınıfından türetilmiştir.