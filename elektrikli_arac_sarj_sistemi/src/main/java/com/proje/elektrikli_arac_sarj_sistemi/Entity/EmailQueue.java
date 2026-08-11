package com.proje.elektrikli_arac_sarj_sistemi.Entity;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EmailQueueStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "email_queue",
    indexes = {
        @Index(name = "idx_email_queue_status_next_attempt",
                columnList = "status, next_attempt_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailQueue extends BaseEntity {

    @Column(nullable = false)
    private UUID invoiceId;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailQueueStatus status;

    @Column(nullable = false)
    private int attemptCount = 0;

    private LocalDateTime nextAttemptAt;

    @Column(length = 2000)
    private String lastError;

    private LocalDateTime sentAt;
}