package com.proje.elektrikli_arac_sarj_sistemi.dto.email;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EmailQueueStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class EmailQueueAdminResponse {

    private UUID id;
    private UUID invoiceId;
    private String invoiceNumber;
    private String recipient;
    private EmailQueueStatus status;
    private int attemptCount;
    private String lastError;
    private LocalDateTime nextAttemptAt;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}