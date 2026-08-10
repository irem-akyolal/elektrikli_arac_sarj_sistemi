package com.proje.elektrikli_arac_sarj_sistemi.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInvoiceEmail(String to, String invoiceNumber) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Elektrikli Araç Şarj Sistemi - Faturanız");
        message.setText(
                "Merhaba,\n\n"
                + "Şarj işleminize ait faturanız oluşturulmuştur.\n\n"
                + "Fatura No: " + invoiceNumber + "\n\n"
                + "İyi günler."
        );

        mailSender.send(message);
    }
}
