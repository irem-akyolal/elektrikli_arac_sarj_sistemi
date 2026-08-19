package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EmailQueueStatus;
import com.proje.elektrikli_arac_sarj_sistemi.dto.email.EmailQueueAdminResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.EmailQueueAdminService;
import com.proje.elektrikli_arac_sarj_sistemi.util.PageableValidator;
import com.proje.elektrikli_arac_sarj_sistemi.util.SortFields;
import com.proje.elektrikli_arac_sarj_sistemi.util.EmailQueueSearchValidator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/email-queue")
@RequiredArgsConstructor
public class EmailQueueAdminController {

    private final EmailQueueAdminService emailQueueAdminService;
    private final PageableValidator pageableValidator;
    private final EmailQueueSearchValidator emailQueueSearchValidator;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @GetMapping
    public ResponseEntity<Page<EmailQueueAdminResponse>> getEmailHistory(

            @RequestParam(required = false)
            EmailQueueStatus status,

            @RequestParam(required = false)
            String recipient,

            @RequestParam(required = false)
            String invoiceNumber,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdAfter,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdBefore,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        emailQueueSearchValidator.validate(
                createdAfter,
                createdBefore
        );

        pageableValidator.validate(
                pageable,
                SortFields.ADMIN_EMAIL_QUEUE
        );

        return ResponseEntity.ok(
                emailQueueAdminService.getEmailHistory(
                        status,
                        recipient,
                        invoiceNumber,
                        createdAfter,
                        createdBefore,
                        pageable
                )
        );
    }
}