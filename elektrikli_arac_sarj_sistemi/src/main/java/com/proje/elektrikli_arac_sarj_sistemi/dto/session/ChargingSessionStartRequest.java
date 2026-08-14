package com.proje.elektrikli_arac_sarj_sistemi.dto.session;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.proje.elektrikli_arac_sarj_sistemi.dto.payment.PaymentCardInfoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

import java.util.UUID;

public class ChargingSessionStartRequest {

    @NotNull(message = "Konnektör ID boş olamaz")
    private UUID connectorId;

    @NotBlank(message = "Plaka boş olamaz")
    private String plateNumber;

    @NotBlank(message = "E-posta boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;


    @NotNull(message = "Provizyon tutarı boş olamaz")
    @DecimalMin(
        value = "0.0",
        inclusive = false,
        message = "Provizyon tutarı 0'dan büyük olmalı") 
                                                         
    private BigDecimal requestedAmount;

    @NotNull(message = "Kart bilgileri boş olamaz")
    @Valid
    private PaymentCardInfoRequest paymentCard;

    public UUID getConnectorId() { return connectorId; }
    public void setConnectorId(UUID connectorId) { this.connectorId = connectorId; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public BigDecimal getRequestedAmount() {
    return requestedAmount;
}

    public void setRequestedAmount(BigDecimal requestedAmount) {
    this.requestedAmount = requestedAmount;
        }

    public PaymentCardInfoRequest getPaymentCard() {
         return paymentCard;
          }

    public void setPaymentCard(PaymentCardInfoRequest paymentCard) {
    this.paymentCard = paymentCard;
     }
}