package com.proje.elektrikli_arac_sarj_sistemi.service.provision;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.ChargingSession;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Provision;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ProvisionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ChargingSessionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ProvisionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.audit.Auditable;
import com.proje.elektrikli_arac_sarj_sistemi.dto.payment.PaymentCardInfoRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.provision.ProvisionCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.provision.ProvisionResponse;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.ProvisionMapper;
import com.proje.elektrikli_arac_sarj_sistemi.payment.PaymentCardInfo;
import com.proje.elektrikli_arac_sarj_sistemi.payment.PaymentProviderClient;
import com.proje.elektrikli_arac_sarj_sistemi.payment.ProvisionAuthorizationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProvisionService {

    private final ProvisionRepository provisionRepository;
    private final ChargingSessionRepository chargingSessionRepository;
    private final ProvisionMapper provisionMapper;
    private final PaymentProviderClient paymentProviderClient; // interface — hangi implementasyon olduğunu bilmiyor
   

    public ProvisionService(ProvisionRepository provisionRepository,
                             ChargingSessionRepository chargingSessionRepository,
                             ProvisionMapper provisionMapper,
                             PaymentProviderClient paymentProviderClient) {
        this.provisionRepository = provisionRepository;
        this.chargingSessionRepository = chargingSessionRepository;
        this.provisionMapper = provisionMapper;
        this.paymentProviderClient = paymentProviderClient;
        
    }
    

    
    @Transactional
    public ProvisionResponse create(ProvisionCreateRequest request) {
        ChargingSession session = chargingSessionRepository.findById(request.getChargingSessionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Şarj oturumu bulunamadı: " + request.getChargingSessionId()));

        provisionRepository.findByChargingSessionId(session.getId()).ifPresent(existing -> {
            throw new BusinessRuleViolationException(
                    "PROVISION_ALREADY_EXISTS",
                    "Bu şarj oturumu için zaten bir provizyon var: " + existing.getId()
            );
        });

        Provision provision = new Provision();
        provision.setChargingSession(session);
        provision.setRequestedAmount(request.getRequestedAmount());
        provision.setStatus(ProvisionStatus.PENDING);

        Provision saved = provisionRepository.save(provision);
        return provisionMapper.toResponse(saved);

    }

        public void validateSessionEligibleForProvision(ChargingSession session) {
        if (session.getStatus() != SessionStatus.STARTED) {
        throw new BusinessRuleViolationException(
                "INVALID_SESSION_STATUS",
                "Sadece STARTED durumundaki oturumlar için provizyon oluşturulabilir. Şu anki durum: " + session.getStatus()
        );
    }
    }


       @Auditable(action = AuditAction.APPROVE, entityType = "PROVISION")
       @Transactional
       public ProvisionResponse approve(UUID id, PaymentCardInfoRequest request) {

        Provision provision = findProvision(id);

       if (provision.getStatus() != ProvisionStatus.PENDING) {
            throw new BusinessRuleViolationException(
                   "INVALID_PROVISION_STATUS",
                    "Sadece PENDING durumundaki provizyonlar onaylanabilir. Şu anki durum: "
                           + provision.getStatus()
           );
       }

        PaymentCardInfo cardInfo = new PaymentCardInfo(
               request.getCardHolderName(),
               request.getCardNumber(),
               request.getExpireMonth(),
               request.getExpireYear(),
              request.getCvc()
       );

        ProvisionAuthorizationResult result =
              paymentProviderClient.authorizeProvision(
                       provision.getRequestedAmount(),
                      cardInfo
               );

        if (!result.isApproved()) {
           provision.setStatus(ProvisionStatus.FAILED);
           provisionRepository.save(provision);

           throw new BusinessRuleViolationException(
                  "PROVISION_AUTHORIZATION_FAILED",
                  "Provizyon ödeme kuruluşu tarafından reddedildi."
           );
       }

       provision.setStatus(ProvisionStatus.APPROVED);
       provision.setProviderReferenceId(result.getProviderReferenceId());

       Provision saved = provisionRepository.save(provision);

       return provisionMapper.toResponse(saved);
  }

    @Transactional
    public ProvisionResponse close(UUID id) {
    Provision provision = findProvision(id);

    if (provision.getStatus() != ProvisionStatus.APPROVED) {
        throw new BusinessRuleViolationException(
                "INVALID_PROVISION_STATUS",
                "Sadece APPROVED durumundaki provizyonlar kapatılabilir. Şu anki durum: "
                        + provision.getStatus()
        );
    }

    provision.setStatus(ProvisionStatus.CLOSED);
    provision.setClosedAt(LocalDateTime.now());

    Provision saved = provisionRepository.save(provision);
    return provisionMapper.toResponse(saved);
   }




   // kullanıcı akışı için provizyon oluşturma otomatikleşti

   @Transactional
   public ProvisionResponse createAndApprove(   
        ProvisionCreateRequest request,
        PaymentCardInfoRequest cardRequest) {

    ChargingSession session = chargingSessionRepository.findById(
            request.getChargingSessionId()
    ).orElseThrow(() -> new ResourceNotFoundException(
            "Şarj oturumu bulunamadı: " + request.getChargingSessionId()
    ));

    validateSessionEligibleForProvision(session);

    provisionRepository.findByChargingSessionId(session.getId())
            .ifPresent(existing -> {
                throw new BusinessRuleViolationException(
                        "PROVISION_ALREADY_EXISTS",
                        "Bu şarj oturumu için zaten bir provizyon var: "
                                + existing.getId()
                );
            });

    Provision provision = new Provision();
    provision.setChargingSession(session);
    provision.setRequestedAmount(request.getRequestedAmount());
    provision.setStatus(ProvisionStatus.PENDING);

    Provision saved = provisionRepository.save(provision);

    PaymentCardInfo cardInfo = new PaymentCardInfo(
            cardRequest.getCardHolderName(),
            cardRequest.getCardNumber(),
            cardRequest.getExpireMonth(),
            cardRequest.getExpireYear(),
            cardRequest.getCvc()
    );

    ProvisionAuthorizationResult result =
            paymentProviderClient.authorizeProvision(
                    saved.getRequestedAmount(),
                    cardInfo
            );

    if (!result.isApproved()) {
        saved.setStatus(ProvisionStatus.FAILED);
        provisionRepository.save(saved);

        throw new BusinessRuleViolationException(
                "PROVISION_AUTHORIZATION_FAILED",
                "Provizyon ödeme kuruluşu tarafından reddedildi."
        );
    }

    saved.setStatus(ProvisionStatus.APPROVED);
    saved.setProviderReferenceId(result.getProviderReferenceId());

    Provision approved = provisionRepository.save(saved);

    return provisionMapper.toResponse(approved);
    }




    public ProvisionResponse getById(UUID id) {
        return provisionMapper.toResponse(findProvision(id));
    }




    private Provision findProvision(UUID id) {
        return provisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provizyon bulunamadı: " + id));
    }
}
