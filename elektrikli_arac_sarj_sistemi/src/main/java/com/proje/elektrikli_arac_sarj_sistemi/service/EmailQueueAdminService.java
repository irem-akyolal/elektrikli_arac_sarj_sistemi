package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.EmailQueue;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EmailQueueRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.email.EmailQueueAdminResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailQueueAdminService {

    private final EmailQueueRepository emailQueueRepository;

    public Page<EmailQueueAdminResponse> getEmailHistory(Pageable pageable) {

        return emailQueueRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private EmailQueueAdminResponse toResponse(EmailQueue emailQueue) {

        EmailQueueAdminResponse response =
                new EmailQueueAdminResponse();

        response.setId(emailQueue.getId());
        response.setInvoiceId(emailQueue.getInvoiceId());
        response.setInvoiceNumber(emailQueue.getInvoiceNumber());
        response.setRecipient(emailQueue.getRecipient());
        response.setStatus(emailQueue.getStatus());
        response.setAttemptCount(emailQueue.getAttemptCount());
        response.setLastError(emailQueue.getLastError());
        response.setNextAttemptAt(emailQueue.getNextAttemptAt());
        response.setSentAt(emailQueue.getSentAt());
        response.setCreatedAt(emailQueue.getCreatedAt());

        return response;
    }
}