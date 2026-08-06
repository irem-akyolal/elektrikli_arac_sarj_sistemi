package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.ChargingSession;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ChargingSessionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.OcpiClient;
import com.proje.elektrikli_arac_sarj_sistemi.ocpi.dto.OcpiCdrDto;
import com.proje.elektrikli_arac_sarj_sistemi.service.session.ChargingSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OcpiCdrProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(OcpiCdrProcessingService.class);

    private final OcpiClient ocpiClient;
    private final ChargingSessionRepository chargingSessionRepository;
    private final ChargingSessionService chargingSessionService;

    public OcpiCdrProcessingService(OcpiClient ocpiClient,
                                     ChargingSessionRepository chargingSessionRepository,
                                     ChargingSessionService chargingSessionService) {
        this.ocpiClient = ocpiClient;
        this.chargingSessionRepository = chargingSessionRepository;
        this.chargingSessionService = chargingSessionService;
    }

    @Transactional
    public void processNewCdrs() {
        List<OcpiCdrDto> cdrs = ocpiClient.fetchNewCdrs();

        for (OcpiCdrDto cdr : cdrs) {
            try {
                processSingleCdr(cdr);
            } catch (Exception ex) {
                logger.error("CDR işlenirken hata oluştu. CDR ID: {}, Session ID: {}, Hata: {}",
                        cdr.getId(), cdr.getSessionId(), ex.getMessage(), ex);
            }
        }
    }

    @Transactional
    public void processCdrForSession(String ocpiSessionId, Double totalEnergy) {
        ChargingSession session = chargingSessionRepository.findByOcpiSessionId(ocpiSessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "OCPI session ID ile eşleşen oturum bulunamadı: " + ocpiSessionId));

        if (session.getStatus() != SessionStatus.STARTED && session.getStatus() != SessionStatus.CHARGING) {
            logger.warn("CDR geldi ama session uygun durumda değil. Session: {}, Durum: {}",
                    ocpiSessionId, session.getStatus());
            return;
        }

        chargingSessionService.completeSession(session.getId(), BigDecimal.valueOf(totalEnergy));
        logger.info("CDR işlendi. Session: {}, Tüketim: {} kWh", ocpiSessionId, totalEnergy);
    }

    private void processSingleCdr(OcpiCdrDto cdr) {
        if (cdr.getSessionId() == null || cdr.getTotalEnergy() == null) {
            logger.warn("Geçersiz CDR atlandı: {}", cdr.getId());
            return;
        }
        processCdrForSession(cdr.getSessionId(), cdr.getTotalEnergy());
    }
}