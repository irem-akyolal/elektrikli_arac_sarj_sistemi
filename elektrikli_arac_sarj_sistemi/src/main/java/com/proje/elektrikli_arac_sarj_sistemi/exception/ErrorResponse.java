package com.proje.elektrikli_arac_sarj_sistemi.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String errorCode;
    private final String message;
    private final String path;
    private final Map<String, String> fieldErrors; // sadece validation hatalarında dolu, diğerlerinde null

    // Genel hatalar için 
    public ErrorResponse(int status, String errorCode, String message, String path) {
        this(status, errorCode, message, path, null);
    }

    // Validation hataları için 
    public ErrorResponse(int status, String errorCode, String message, String path, Map<String, String> fieldErrors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
}