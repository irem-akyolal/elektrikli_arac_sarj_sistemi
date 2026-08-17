package com.proje.elektrikli_arac_sarj_sistemi.controller;

import com.proje.elektrikli_arac_sarj_sistemi.dto.email.EmailQueueAdminResponse;
import com.proje.elektrikli_arac_sarj_sistemi.service.EmailQueueAdminService;
import com.proje.elektrikli_arac_sarj_sistemi.util.PageableValidator;
import com.proje.elektrikli_arac_sarj_sistemi.util.SortFields;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/email-queue")
@RequiredArgsConstructor
public class EmailQueueAdminController {

    private final EmailQueueAdminService emailQueueAdminService;
    private final PageableValidator pageableValidator;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR')")
    @GetMapping
    public ResponseEntity<Page<EmailQueueAdminResponse>> getEmailHistory(
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        pageableValidator.validate(
                pageable,
                SortFields.ADMIN_EMAIL_QUEUE
        );

        return ResponseEntity.ok(
                emailQueueAdminService.getEmailHistory(pageable)
        );
    }
}