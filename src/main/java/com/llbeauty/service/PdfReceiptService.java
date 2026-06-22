package com.llbeauty.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.llbeauty.entity.Appointment;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;

@Service
public class PdfReceiptService {

    public byte[] generateSalonReceipt(Appointment appointment) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLACK);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
            Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

            // Title
            Paragraph title = new Paragraph("EVA LL Beauty - Salon Receipt", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Customer Info
            Paragraph customerInfo = new Paragraph();
            customerInfo.add(new Phrase("Customer Name: ", boldFont));
            customerInfo.add(new Phrase(appointment.getUserName() + "\n", regularFont));
            customerInfo.add(new Phrase("Mobile Number: ", boldFont));
            customerInfo.add(new Phrase(appointment.getUserMobile() + "\n", regularFont));
            customerInfo.setSpacingAfter(15);
            document.add(customerInfo);

            // Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addTableRow(table, "Booking ID", String.valueOf(appointment.getId()), boldFont, regularFont);
            
            String dateStr = appointment.getAppointmentDate() != null ? appointment.getAppointmentDate().toString() : "N/A";
            addTableRow(table, "Date", dateStr, boldFont, regularFont);
            addTableRow(table, "Time Slot", appointment.getTimeSlot(), boldFont, regularFont);
            addTableRow(table, "Services", appointment.getServices(), boldFont, regularFont);
            
            double totalAmount = appointment.getTotalAmount() != null ? appointment.getTotalAmount() : 0.0;
            double advancePaid = appointment.getAdvancePaid() != null ? appointment.getAdvancePaid() : 0.0;
            double remaining = totalAmount - advancePaid;
            
            addTableRow(table, "Total Amount", "Rs. " + totalAmount, boldFont, regularFont);
            addTableRow(table, "Advance Paid", "Rs. " + advancePaid, boldFont, regularFont);
            addTableRow(table, "Remaining Amount", "Rs. " + remaining, boldFont, regularFont);
            
            addTableRow(table, "Booking Status", appointment.getStatus(), boldFont, regularFont);
            addTableRow(table, "Payment Status", appointment.getPaymentStatus(), boldFont, regularFont);

            document.add(table);

            // Footer
            Paragraph footer = new Paragraph("Thank you for choosing EVA LL Beauty!", subTitleFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private void addTableRow(PdfPTable table, String label, String value, Font boldFont, Font regularFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setPadding(8);
        labelCell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", regularFont));
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }
}
