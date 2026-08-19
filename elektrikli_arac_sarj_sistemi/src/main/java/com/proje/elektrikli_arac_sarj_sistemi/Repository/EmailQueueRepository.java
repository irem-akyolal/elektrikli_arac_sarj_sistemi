package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.EmailQueue;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EmailQueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EmailQueueRepository extends JpaRepository<EmailQueue, UUID>, JpaSpecificationExecutor<EmailQueue> {

    List<EmailQueue> findTop20ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            EmailQueueStatus status,
            LocalDateTime now
    );
}