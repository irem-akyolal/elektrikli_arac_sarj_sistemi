package com.proje.elektrikli_arac_sarj_sistemi.Entity;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PaymentProviderType;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(
    name = "payments",
    indexes = {
        @Index(name = "idx_payment_transaction_id", columnList = "transactionId")
    }
)
@Getter
@Setter
@NoArgsConstructor 
@SQLRestriction("deleted_at IS NULL")
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provision_id", nullable = false, unique = true)
    private Provision provision;

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProviderType providerType; // entity değil enum

    private String transactionId;
}
