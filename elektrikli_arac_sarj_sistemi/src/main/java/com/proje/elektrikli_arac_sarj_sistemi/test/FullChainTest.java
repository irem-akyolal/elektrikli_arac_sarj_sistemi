package com.proje.elektrikli_arac_sarj_sistemi.test;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.*;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.*;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class FullChainTest {

    @Bean
    CommandLineRunner testFullChain(
            LocationRepository locationRepository,
            EvseRepository evseRepository,
            ConnectorRepository connectorRepository,
            ChargingSessionRepository chargingSessionRepository,
            ProvisionRepository provisionRepository,
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository
    ) {
        return args -> {

            // 1) Location — varsa bul, yoksa oluştur (tekrar çalıştırılabilir olsun diye)
            Location location = locationRepository.findByOcpiLocationId("TR*TEST*L001")
                    .orElseGet(() -> {
                        Location l = new Location();
                        l.setOcpiLocationId("TR*TEST*L001");
                        l.setName("Test İstasyonu");
                        l.setAddress("Test Mahallesi, Test Sokak No:1");
                        l.setLatitude(41.0082);
                        l.setLongitude(28.9784);
                        l.setActive(true);
                        return locationRepository.save(l);
                    });
            System.out.println("Location hazır: " + location.getId());

            // 2) Evse — varsa bul, yoksa oluştur
            Evse evse = evseRepository.findByOcpiEvseUid("TEST-EVSE-UID-001")
                    .orElseGet(() -> {
                        Evse e = new Evse();
                        e.setOcpiEvseUid("TEST-EVSE-UID-001");
                        e.setEvseId("TR*TEST*E001");
                        e.setLocation(location);
                        e.setStatus(EvseStatus.AVAILABLE);
                        return evseRepository.save(e);
                    });
            System.out.println("Evse hazır: " + evse.getId());

            // 3) Connector — varsa bul, yoksa oluştur
            Connector connector = connectorRepository.findByOcpiConnectorId("TR*TEST*C001")
                    .orElseGet(() -> {
                        Connector c = new Connector();
                        c.setOcpiConnectorId("TR*TEST*C001");
                        c.setEvse(evse);
                        c.setStandard(ConnectorStandard.IEC_62196_T2);
                        c.setFormat(ConnectorFormat.SOCKET);
                        c.setPowerType(PowerType.AC_3_PHASE);
                        c.setUnitPrice(BigDecimal.valueOf(8.5));
                        return connectorRepository.save(c);
                    });
            System.out.println("Connector hazır: " + connector.getId());

            // 4) ChargingSession — HER ÇALIŞTIRMADA yeni bir session oluşturuyoruz
            // (session'lar tekil olmak zorunda değil, gerçek hayatta zaten çok sayıda olur)
            ChargingSession session = new ChargingSession();
            session.setConnector(connector);
            session.setPlateNumber("34TEST34");
            session.setEmail("test@example.com");
            session.setOcpiSessionId("OCPI-SESSION-" + System.currentTimeMillis()); // her seferinde tekil olsun
            session.setStatus(SessionStatus.CLOSED);
            session.setStartedAt(LocalDateTime.now().minusHours(2));
            session.setCompletedAt(LocalDateTime.now().minusHours(1));
            session.setConnectorRemovedAt(LocalDateTime.now().minusMinutes(45));
            session.setEnergyConsumedKwh(BigDecimal.valueOf(42.5));
            chargingSessionRepository.save(session);
            System.out.println("ChargingSession kaydedildi: " + session.getId());

            // 5) Provision
            Provision provision = new Provision();
            provision.setChargingSession(session);
            provision.setRequestedAmount(BigDecimal.valueOf(500));
            provision.setStatus(ProvisionStatus.CLOSED);
            provision.setProviderReferenceId("PROV-REF-" + System.currentTimeMillis());
            provision.setClosedAt(LocalDateTime.now().minusMinutes(50));
            provisionRepository.save(provision);
            System.out.println("Provision kaydedildi: " + provision.getId());

            // 6) Payment
            Payment payment = new Payment();
            payment.setProvision(provision);
            payment.setAmount(BigDecimal.valueOf(361.25)); // gerçek tüketim 42.5 kWh * 8.5 TL
            payment.setRefundAmount(BigDecimal.valueOf(500).subtract(BigDecimal.valueOf(361.25)));
            payment.setStatus(PaymentStatus.CAPTURED);
            payment.setProviderType(PaymentProviderType.IYZICO);
            payment.setTransactionId("TXN-" + System.currentTimeMillis());
            paymentRepository.save(payment);
            System.out.println("Payment kaydedildi: " + payment.getId());

            // 7) Invoice
            Invoice invoice = new Invoice();
            invoice.setChargingSession(session);
            invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
            invoice.setSubTotal(BigDecimal.valueOf(301.04)); // KDV hariç (yaklaşık)
            invoice.setTaxRate(BigDecimal.valueOf(0.20));
            invoice.setTaxAmount(BigDecimal.valueOf(60.21));
            invoice.setAmount(BigDecimal.valueOf(361.25));
            invoice.setEmail("test@example.com");
            invoice.setStatus(InvoiceStatus.CREATED);
            invoiceRepository.save(invoice);
            System.out.println("Invoice kaydedildi: " + invoice.getId());

            System.out.println("--- TAM ZİNCİR (Location → Evse → Connector → Session → Provision → Payment → Invoice) BAŞARIYLA OLUŞTU ---");
        };
    }
}