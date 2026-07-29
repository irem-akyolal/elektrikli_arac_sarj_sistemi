package com.proje.elektrikli_arac_sarj_sistemi.Entity;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ProvisionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "provisions")
@Getter
@Setter
@NoArgsConstructor 
@SQLRestriction("deleted_at IS NULL")
public class Provision extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_session_id", nullable = false, unique = true)
    private ChargingSession chargingSession;

    @Column(nullable = false)
    private BigDecimal requestedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProvisionStatus status;

    private String providerReferenceId;

    private LocalDateTime closedAt;

    @OneToOne(mappedBy = "provision", cascade = CascadeType.ALL)
    private Payment payment;
}