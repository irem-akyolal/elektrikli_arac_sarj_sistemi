package com.proje.elektrikli_arac_sarj_sistemi.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInvoiceEmail(
            String to,
            String invoiceNumber,
            String pdfPath) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);

            helper.setSubject(
                    "Elektrikli Araç Şarj Sistemi - Faturanız"
            );

            helper.setText(
                    "Merhaba,\n\n"
                    + "Şarj işleminize ait faturanız oluşturulmuştur.\n\n"
                    + "Fatura No: " + invoiceNumber + "\n\n"
                    + "Faturanızı PDF olarak ekte bulabilirsiniz.\n\n"
                    + "İyi günler."
            );

            File pdfFile = new File(pdfPath);

            if (!pdfFile.exists()) {
                throw new IllegalStateException(
                        "Fatura PDF dosyası bulunamadı: " + pdfPath
                );
            }

            FileSystemResource pdfResource =
                    new FileSystemResource(pdfFile);

            helper.addAttachment(
                    "Fatura-" + invoiceNumber + ".pdf",
                    pdfResource
            );

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new IllegalStateException(
                    "Fatura emaili oluşturulamadı.",
                    e
            );
        }
    }
}