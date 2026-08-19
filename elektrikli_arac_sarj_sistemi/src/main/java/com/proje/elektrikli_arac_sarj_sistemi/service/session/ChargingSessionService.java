package com.proje.elektrikli_arac_sarj_sistemi.service.session;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.ChargingSession;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Provision;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ChargingSessionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ConnectorRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EvseRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ProvisionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.payment.PaymentCardInfoRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.provision.ProvisionCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.provision.ProvisionResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.session.ChargingSessionResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.session.ChargingSessionStartRequest;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.ChargingSessionMapper;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.OcpiClient;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.StartSessionResult;
import com.proje.elektrikli_arac_sarj_sistemi.service.payment.PaymentService;
import com.proje.elektrikli_arac_sarj_sistemi.service.provision.ProvisionService;
import com.proje.elektrikli_arac_sarj_sistemi.payment.PaymentProviderClient;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.time.Duration;

@Service
public class ChargingSessionService {

     private final ChargingSessionRepository chargingSessionRepository;
     private final ConnectorRepository connectorRepository;
     private final EvseRepository evseRepository;
     private final ProvisionRepository provisionRepository;
     private final ChargingSessionMapper chargingSessionMapper;
     private final PaymentService paymentService; // otomatik tahsilat için
     private final PaymentProviderClient paymentProviderClient;
     private final ProvisionService provisionService;
     private final OcpiClient ocpiClient; // dışarı akışı (sistem → CPO, Remote Start/Stop) Şarj Başlat" dediğimizde, gerçekten OCPI'ye (mock'a) bir istek gidecek
     private final java.util.Optional<com.proje.elektrikli_arac_sarj_sistemi.ocpi.MockOcpiClient> mockOcpiClient;
     
     
    public ChargingSessionService(ChargingSessionRepository chargingSessionRepository,
                                   ConnectorRepository connectorRepository,
                                   EvseRepository evseRepository,
                                   ProvisionRepository provisionRepository,
                                   ChargingSessionMapper chargingSessionMapper,
                                   PaymentService paymentService,
                                   ProvisionService provisionService,
                                   PaymentProviderClient paymentProviderClient,
                                   OcpiClient ocpiClient,
                                   java.util.Optional<com.proje.elektrikli_arac_sarj_sistemi.ocpi.MockOcpiClient> mockOcpiClient) {
        this.chargingSessionRepository = chargingSessionRepository;
        this.connectorRepository = connectorRepository;
        this.evseRepository= evseRepository;
        this.provisionRepository = provisionRepository;
        this.chargingSessionMapper = chargingSessionMapper;
        this.paymentService = paymentService;
        this.ocpiClient = ocpiClient;
        this.provisionService=provisionService;
        this.paymentProviderClient = paymentProviderClient;
        this.mockOcpiClient = mockOcpiClient;
        

        


    }

@Transactional
public ChargingSessionResponse startSession(
        ChargingSessionStartRequest request) {

    // =====================================================
    // 1. CONNECTOR KONTROLÜ
    // =====================================================

    Connector connector = connectorRepository.findById(
            request.getConnectorId()
    ).orElseThrow(() -> new ResourceNotFoundException(
            "Konnektör bulunamadı: " + request.getConnectorId()
    ));

    validateConnectorAvailable(connector);


    // =====================================================
    // 2. GEÇİCİ CHARGING SESSION OLUŞTUR
    // =====================================================
    // Henüz OCPI Remote Start gönderilmedi.
    // Bu aşamada sadece sistemimizde session kaydı oluşturuyoruz.

    ChargingSession session = new ChargingSession();

    session.setConnector(connector);
    session.setPlateNumber(request.getPlateNumber());
    session.setEmail(request.getEmail());
    session.setStatus(SessionStatus.STARTED);
    session.setStartedAt(LocalDateTime.now());

    ChargingSession savedSession =
            chargingSessionRepository.save(session);


    // =====================================================
    // 3. PROVİZYON OLUŞTUR
    // =====================================================

    ProvisionCreateRequest provisionRequest =
            new ProvisionCreateRequest();

    provisionRequest.setChargingSessionId(
            savedSession.getId()
    );

    provisionRequest.setRequestedAmount(
            request.getRequestedAmount()
    );

    // Kart bilgileri
    provisionRequest.setCardHolderName(
            request.getPaymentCard().getCardHolderName()
    );

    provisionRequest.setCardNumber(
            request.getPaymentCard().getCardNumber()
    );

    provisionRequest.setExpireMonth(
            request.getPaymentCard().getExpireMonth()
    );

    provisionRequest.setExpireYear(
            request.getPaymentCard().getExpireYear()
    );

    provisionRequest.setCvc(
            request.getPaymentCard().getCvc()
    );


    // =====================================================
    // 4. PROVİZYON KAYDI OLUŞTUR
    // =====================================================

    ProvisionResponse provision =
            provisionService.create(provisionRequest);


    // =====================================================
    // 5. PRE-AUTH / PROVİZYON ONAYI
    // =====================================================

    PaymentCardInfoRequest cardRequest =
            request.getPaymentCard();

    ProvisionResponse approvedProvision =
            provisionService.approve(
                    provision.getId(),
                    cardRequest
            );


    // =====================================================
    // 6. PRE-AUTH BAŞARILIYSA OCPI REMOTE START
    // =====================================================

    StartSessionResult ocpiResult =
            ocpiClient.startSession(
                    connector.getEvse().getOcpiEvseUid(),
                    extractConnectorId(
                            connector.getOcpiConnectorId()
                    )
            );

    if (!ocpiResult.isAccepted()) {

    boolean cancelled =
            paymentProviderClient.cancelProvision(
                    approvedProvision.getProviderReferenceId()
            );

    if (!cancelled) {
        throw new BusinessRuleViolationException(
                "PROVISION_CANCEL_FAILED",
                "CPO şarj başlatma isteğini reddetti ve "
                        + "Iyzico provizyonu iptal edilemedi."
        );
    }

    throw new BusinessRuleViolationException(
            "OCPI_START_REJECTED",
            "CPO şarj başlatma isteğini reddetti. "
                    + "Iyzico provizyonu iptal edildi."
    );
}


    /*
         * Burada PRE-AUTH başarılı olmuş fakat
         * OCPI Remote Start başarısız olmuş olabilir.
         *
         * Bu durumda ileride authorization release/
         * cancellation mekanizması eklenmesi gerekir.
         */


    // =====================================================
    // 7. OCPI SESSION ID'Yİ KAYDET
    // =====================================================

    savedSession.setOcpiSessionId(
            ocpiResult.getOcpiSessionId()
    );

    ChargingSession finalSession =
            chargingSessionRepository.save(savedSession);


    // =====================================================
    // 8. EVSE DURUMUNU CHARGING YAP
    // =====================================================

    Evse evse = connector.getEvse();

    evse.setStatus(EvseStatus.CHARGING);

    evseRepository.save(evse);


    // =====================================================
    // 8.5 MOCK ORTAMDA TÜKETİM İZLEMEYE AL (11. madde)
    // =====================================================

    mockOcpiClient.ifPresent(mock -> mock.registerSessionForMonitoring(
            ocpiResult.getOcpiSessionId(),
            request.getRequestedAmount(),
            connector.getUnitPrice(),
            connector.getMaxElectricPowerWatt()
    ));



    // =====================================================
    // 9. SESSION'I GERİ DÖNDÜR
    // =====================================================

    return chargingSessionMapper.toResponse(finalSession);
}




    public ChargingSessionResponse getById(UUID id) {
        ChargingSession session = findSession(id);
        return chargingSessionMapper.toResponse(session);
    }






@Transactional
public ChargingSessionResponse markAsCharging(UUID id) {
    ChargingSession session = findSession(id);

    if (session.getStatus() != SessionStatus.STARTED) {
        throw new BusinessRuleViolationException(
                "INVALID_SESSION_STATUS",
                "Sadece STARTED durumundaki oturumlar CHARGING durumuna geçebilir. Şu anki durum: " + session.getStatus()
        );
    }

    session.setStatus(SessionStatus.CHARGING);
    ChargingSession saved = chargingSessionRepository.save(session);
    return chargingSessionMapper.toResponse(saved);
}



@Transactional
public ChargingSessionResponse completeSession(
        UUID id,
        java.math.BigDecimal energyConsumedKwh) {

    ChargingSession session = findSession(id);

    // Şarj yalnızca gerçekten CHARGING durumundaysa tamamlanabilir.
    if (session.getStatus() != SessionStatus.CHARGING) {
        throw new BusinessRuleViolationException(
                "INVALID_SESSION_STATUS",
                "Sadece CHARGING durumundaki oturumlar tamamlanabilir. Şu anki durum: "
                        + session.getStatus()
        );
    }

    session.setStatus(SessionStatus.COMPLETED);
    session.setCompletedAt(LocalDateTime.now());
    session.setEnergyConsumedKwh(energyConsumedKwh);

    ChargingSession saved =
            chargingSessionRepository.save(session);

    // Şarj tamamlandı ancak kablo henüz çıkarılmadı.
    Evse evse = session.getConnector().getEvse();
    evse.setStatus(EvseStatus.PENDING_REMOVAL);
    evseRepository.save(evse);

    return chargingSessionMapper.toResponse(saved);
}


@Transactional
public ChargingSessionResponse markConnectorRemoved(UUID id) {
    ChargingSession session = findSession(id);

    if (session.getStatus() != SessionStatus.COMPLETED) {
        throw new BusinessRuleViolationException(
                "INVALID_SESSION_STATUS",
                "Sadece COMPLETED durumundaki oturumlarda konnektör çıkarma işlemi yapılabilir. Şu anki durum: " + session.getStatus()
        );
    }


    ocpiClient.stopSession(session.getOcpiSessionId()); // CPO'ya bilgi ver
    LocalDateTime removedAt = LocalDateTime.now();
    session.setConnectorRemovedAt(removedAt);

      long durationSeconds =
        Duration.between(
                session.getCompletedAt(),
                removedAt
        ).getSeconds();

    session.setPendingRemovalDurationSeconds(durationSeconds);

    session.setStatus(SessionStatus.CLOSED); 

    ChargingSession saved = chargingSessionRepository.save(session);

    Evse evse = session.getConnector().getEvse();
    evse.setStatus(EvseStatus.AVAILABLE); 
    evseRepository.save(evse);

    // Otomatik akış: bu session'a bağlı provizyonu bul, tahsilatı otomatik tetikle
        Provision provision = provisionRepository.findByChargingSessionId(session.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bu oturuma bağlı provizyon bulunamadı: " + session.getId()));
        paymentService.captureForProvision(provision.getId());

        return chargingSessionMapper.toResponse(saved);
}

    // ============================
    // Private Methods
    // ============================

    private void validateConnectorAvailable(Connector connector) {
        Evse evse = connector.getEvse();
        if (evse.getStatus() != EvseStatus.AVAILABLE) {
            throw new BusinessRuleViolationException(
                    "CONNECTOR_NOT_AVAILABLE",
                    "Bu konnektör şu an müsait değil. Durum: " + evse.getStatus()
            );
        }
    }

    private ChargingSession findSession(UUID id) {
        return chargingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Şarj oturumu bulunamadı: " + id));
    }


    
    private String extractConnectorId(String ocpiConnectorId) {
    // "evseUid-connectorId" formatında birleştirmiştik, geri ayırıyoruz
    int lastDashIndex = ocpiConnectorId.lastIndexOf('-');
    return ocpiConnectorId.substring(lastDashIndex + 1);
}
}
