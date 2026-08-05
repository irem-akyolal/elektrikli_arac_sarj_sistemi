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
import com.proje.elektrikli_arac_sarj_sistemi.dto.session.ChargingSessionResponse;
import com.proje.elektrikli_arac_sarj_sistemi.dto.session.ChargingSessionStartRequest;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.ChargingSessionMapper;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.OcpiClient;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.StartSessionResult;
import com.proje.elektrikli_arac_sarj_sistemi.service.payment.PaymentService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ChargingSessionService {

     private final ChargingSessionRepository chargingSessionRepository;
     private final ConnectorRepository connectorRepository;
     private final EvseRepository evseRepository;
     private final ProvisionRepository provisionRepository;
     private final ChargingSessionMapper chargingSessionMapper;
     private final PaymentService paymentService; // otomatik tahsilat için
     private final OcpiClient ocpiClient; // dışarı akışı (sistem → CPO, Remote Start/Stop) Şarj Başlat" dediğimizde, gerçekten OCPI'ye (mock'a) bir istek gidecek

    public ChargingSessionService(ChargingSessionRepository chargingSessionRepository,
                                   ConnectorRepository connectorRepository,
                                   EvseRepository evseRepository,
                                   ProvisionRepository provisionRepository,
                                   ChargingSessionMapper chargingSessionMapper,
                                   PaymentService paymentService,
                                   OcpiClient ocpiClient) {
        this.chargingSessionRepository = chargingSessionRepository;
        this.connectorRepository = connectorRepository;
        this.evseRepository= evseRepository;
        this.provisionRepository = provisionRepository;
        this.chargingSessionMapper = chargingSessionMapper;
        this.paymentService = paymentService;
        this.ocpiClient = ocpiClient;

    }

  @Transactional
public ChargingSessionResponse startSession(ChargingSessionStartRequest request) {
    Connector connector = connectorRepository.findById(request.getConnectorId())
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Konnektör bulunamadı: " + request.getConnectorId()));

    validateConnectorAvailable(connector);


       // OCPI'ye Remote Start isteği gönder
    StartSessionResult ocpiResult = ocpiClient.startSession(
            connector.getEvse().getOcpiEvseUid(),
            extractConnectorId(connector.getOcpiConnectorId())
    );

    if (!ocpiResult.isAccepted()) {
        throw new BusinessRuleViolationException(
                "OCPI_START_REJECTED",
                "CPO şarj başlatma isteğini reddetti."
        );
    }

    ChargingSession session = new ChargingSession();
    session.setConnector(connector);
    session.setPlateNumber(request.getPlateNumber());
    session.setEmail(request.getEmail());
    session.setStatus(SessionStatus.STARTED);
    session.setStartedAt(LocalDateTime.now());

    ChargingSession saved = chargingSessionRepository.save(session);

    Evse evse = connector.getEvse();
    evse.setStatus(EvseStatus.CHARGING);
    evseRepository.save(evse); 

    return chargingSessionMapper.toResponse(saved);
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
public ChargingSessionResponse completeSession(UUID id, java.math.BigDecimal energyConsumedKwh) {
    ChargingSession session = findSession(id);

    if (session.getStatus() != SessionStatus.STARTED && session.getStatus() != SessionStatus.CHARGING) {
        throw new BusinessRuleViolationException(
                "INVALID_SESSION_STATUS",
                "Sadece aktif oturumlar tamamlanabilir. Şu anki durum: " + session.getStatus()
        );
    }

    session.setStatus(SessionStatus.COMPLETED);
    session.setCompletedAt(LocalDateTime.now());
    session.setEnergyConsumedKwh(energyConsumedKwh);
    ChargingSession saved = chargingSessionRepository.save(session);

    // EVSE artık "müsait" değil, "fişin çekilmesi bekleniyor" durumunda
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
    
    session.setConnectorRemovedAt(LocalDateTime.now());
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
