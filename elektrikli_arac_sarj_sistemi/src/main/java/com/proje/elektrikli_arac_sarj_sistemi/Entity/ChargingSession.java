package com.proje.elektrikli_arac_sarj_sistemi.Entity;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "charging_sessions",
    indexes = {
        @Index(name = "idx_charging_session_email", columnList = "email"),
        @Index(name = "idx_charging_session_plate", columnList = "plateNumber"),
        @Index(name = "idx_charging_session_ocpi_id", columnList = "ocpiSessionId")
    }
)
@Getter
@Setter
@NoArgsConstructor 
public class ChargingSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connector_id", nullable = false)
    private Connector connector;

    @Column(nullable = false)
    private String plateNumber;

    @Column(nullable = false)
    private String email;

    private String ocpiSessionId; // CPO tarafındaki oturum referansı

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime connectorRemovedAt;

    private BigDecimal energyConsumedKwh;

    @OneToOne(mappedBy = "chargingSession", cascade = CascadeType.ALL)
    private Provision provision;
}