package com.proje.elektrikli_arac_sarj_sistemi.service.invoice;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Invoice;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InvoicePdfService {

    private static final String PDF_DIRECTORY = "uploads/invoices/";

    public String generateInvoicePdf(Invoice invoice) {

        File directory = new File(PDF_DIRECTORY);

        if (!directory.exists()) {
            boolean created = directory.mkdirs();

            if (!created) {
                throw new IllegalStateException(
                        "Fatura PDF klasörü oluşturulamadı: " + PDF_DIRECTORY
                );
            }
        }

        String fileName = invoice.getInvoiceNumber() + ".pdf";
        String filePath = PDF_DIRECTORY + fileName;

        Document document = new Document();

        try {
            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(filePath)
            );

            document.open();

            // Başlık
            Paragraph title = new Paragraph(
                    "ELEKTRİKLİ ARAÇ ŞARJ SİSTEMİ"
            );
            document.add(title);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("FATURA"));
            document.add(new Paragraph(" "));

            // Fatura bilgileri
            document.add(
                    new Paragraph(
                            "Fatura No: " + invoice.getInvoiceNumber()
                    )
            );

            document.add(
                    new Paragraph(
                            "Tarih: " +
                            LocalDateTime.now()
                                    .format(DateTimeFormatter.ofPattern(
                                            "dd.MM.yyyy HH:mm"
                                    ))
                    )
            );

            document.add(
                    new Paragraph(
                            "E-posta: " + invoice.getEmail()
                    )
            );

            document.add(new Paragraph(" "));

            // Tutar tablosu
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            addRow(table, "KDV Hariç Tutar",
                    formatAmount(invoice.getSubTotal()));

            addRow(table, "KDV Oranı",
                    invoice.getTaxRate()
                            .multiply(BigDecimal.valueOf(100))
                            .stripTrailingZeros()
                            .toPlainString()
                            + "%");

            addRow(table, "KDV Tutarı",
                    formatAmount(invoice.getTaxAmount()));

            addRow(table, "TOPLAM",
                    formatAmount(invoice.getAmount()));

            document.add(table);

            document.add(new Paragraph(" "));

            document.add(
                    new Paragraph(
                            "Şarj işleminiz için oluşturulan elektronik faturadır."
                    )
            );

            document.add(
                    new Paragraph(
                            "Teşekkür ederiz."
                    )
            );

        } catch (DocumentException | IOException e) {

            throw new IllegalStateException(
                    "Fatura PDF'i oluşturulamadı: "
                            + invoice.getInvoiceNumber(),
                    e
            );

        } finally {

            document.close();
        }

        return filePath;
    }

    private void addRow(
            PdfPTable table,
            String label,
            String value) {

        table.addCell(new PdfPCell(new Phrase(label)));
        table.addCell(new PdfPCell(new Phrase(value)));
    }

    private String formatAmount(BigDecimal amount) {

        return amount
                .setScale(2)
                .toPlainString() + " TL";
    }
}