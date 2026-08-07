package com.proje.elektrikli_arac_sarj_sistemi.exception;

public class InvalidPageRequestException extends ApplicationException {
    public InvalidPageRequestException(String message) {
        super("INVALID_PAGE_REQUEST", message);
    }
}