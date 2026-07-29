package com.proje.elektrikli_arac_sarj_sistemi.Entity;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorFormat;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PowerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "connectors")
@Getter
@Setter
@NoArgsConstructor 
@SQLRestriction("deleted_at IS NULL")
public class Connector extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String ocpiConnectorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evse_id", nullable = false)
    private Evse evse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectorStandard standard; // OCPI ConnectorType karşılığı

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectorFormat format; // SOCKET / CABLE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PowerType powerType; // AC_1_PHASE, AC_3_PHASE, DC

    private Integer maxVoltage;
    private Integer maxAmperage;
    private Integer maxElectricPowerWatt;

    // Bizim kendi iş mantığımıza ait, OCPI spesifikasyonunda yok
    @Column(nullable = false)
    private BigDecimal unitPrice;
}