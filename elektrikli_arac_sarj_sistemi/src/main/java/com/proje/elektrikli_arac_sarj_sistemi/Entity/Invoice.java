package com.proje.elektrikli_arac_sarj_sistemi.Entity;


import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "invoices",
    indexes = {
        @Index(name = "idx_invoice_email", columnList = "email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class Invoice extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_session_id", nullable = false, unique = true)
    private ChargingSession chargingSession;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    // Tutar kırılımı — yasal fatura için gerekli
    @Column(nullable = false)
    private BigDecimal subTotal;      // KDV hariç tutar

    @Column(nullable = false)
    private BigDecimal taxRate;       //  0.20

    @Column(nullable = false)
    private BigDecimal taxAmount;     // hesaplanan KDV tutarı

    @Column(nullable = false)
    private BigDecimal amount;        // subTotal + taxAmount (toplam)

    @Column(nullable = false)
    private String email;

    private String pdfPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    private LocalDateTime sentAt;
}