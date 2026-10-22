package com.waterbilling.backend.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.entity.Resident;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoicePdfService {

    // =========================
    // GENERATE PDF INVOICE
    // =========================
    public byte[] generateInvoicePdf(Bill bill) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            // ============ HEADER ============
            Paragraph title = new Paragraph("Water Billing Invoice")
                    .setFontSize(22)
                    .setBold()
                    .setFontColor(new DeviceRgb(41, 128, 185))
                    .setTextAlignment(TextAlignment.CENTER);
            doc.add(title);

            doc.add(new Paragraph("Water Billing Management System")
                    .setFontSize(11)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("\n"));

            // ============ BILL INFO ============
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth();

            infoTable.addCell(createInfoCell("Bill Number:", bill.getBillNumber()));
            infoTable.addCell(createInfoCell("Billing Month:", bill.getBillingMonth()));

            infoTable.addCell(createInfoCell("Bill Date:",
                    bill.getBillDate() != null
                            ? bill.getBillDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                            : "N/A"));

            infoTable.addCell(createInfoCell("Due Date:",
                    bill.getDueDate() != null
                            ? bill.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                            : "N/A"));

            doc.add(infoTable);

            doc.add(new Paragraph("\n"));

            // ============ RESIDENT INFO ============
            Resident resident = (bill.getProperty() != null)
                    ? bill.getProperty().getResident() : null;

            if (resident != null) {
                Paragraph residentTitle = new Paragraph("Resident Details")
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(new DeviceRgb(41, 128, 185));
                doc.add(residentTitle);

                Table residentTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                        .useAllAvailableWidth();
                residentTable.addCell(createInfoCell("Name:", resident.getName()));
                residentTable.addCell(createInfoCell("Email:", resident.getEmail()));
                residentTable.addCell(createInfoCell("Phone:", resident.getPhone()));
                residentTable.addCell(createInfoCell("Address:", resident.getAddress()));
                doc.add(residentTable);

                doc.add(new Paragraph("\n"));
            }

            // ============ CHARGES TABLE ============
            Paragraph chargesTitle = new Paragraph("Bill Breakdown")
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(new DeviceRgb(41, 128, 185));
            doc.add(chargesTitle);

            Table chargesTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .useAllAvailableWidth();

            chargesTable.addHeaderCell(createHeaderCell("Description"));
            chargesTable.addHeaderCell(createHeaderCell("Amount (Rs.)")); // Changed here

            chargesTable.addCell(createBodyCell("Water Charges"));
            chargesTable.addCell(createBodyCell(
                    String.valueOf(bill.getWaterCharges())));

            chargesTable.addCell(createBodyCell("Fixed Charges"));
            chargesTable.addCell(createBodyCell(
                    String.valueOf(bill.getFixedCharges())));

            chargesTable.addCell(createBodyCell("Shared Cost Allocation"));
            chargesTable.addCell(createBodyCell(
                    String.valueOf(bill.getSharedCost() != null ? bill.getSharedCost() : 0)));

            // Total row (bold)
            Cell totalLabel = new Cell()
                    .add(new Paragraph("TOTAL AMOUNT").setBold())
                    .setBackgroundColor(new DeviceRgb(240, 248, 255));
            Cell totalValue = new Cell()
                    .add(new Paragraph("Rs. " + bill.getTotalAmount()).setBold() // Changed here
                            .setFontColor(new DeviceRgb(41, 128, 185)))
                    .setBackgroundColor(new DeviceRgb(240, 248, 255));
            chargesTable.addCell(totalLabel);
            chargesTable.addCell(totalValue);

            doc.add(chargesTable);

            doc.add(new Paragraph("\n\n"));

            // ============ PAYMENT INSTRUCTIONS ============
            Paragraph instructions = new Paragraph()
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(new DeviceRgb(41, 128, 185));
            instructions.add("Payment Instructions:\n");
            doc.add(instructions);

            doc.add(new Paragraph("- Please pay the bill before the due date.")
                    .setFontSize(10));
            doc.add(new Paragraph("- Payment methods: UPI, Net Banking, Cash.")
                    .setFontSize(10));
            doc.add(new Paragraph("- Late payment may incur additional charges.")
                    .setFontSize(10));

            doc.add(new Paragraph("\n\n"));

            // ============ FOOTER ============
            doc.add(new Paragraph("Thank you for using Water Billing Management System!")
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            System.err.println("PDF generation error: " + e.getMessage());
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    // ============ HELPER METHODS ============
    private Cell createInfoCell(String label, String value) {
        if (value == null) value = "N/A";
        return new Cell()
                .add(new Paragraph(label).setBold().setFontSize(10))
                .add(new Paragraph(value).setFontSize(10))
                .setBorder(null)
                .setPadding(5);
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold()
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(new DeviceRgb(41, 128, 185))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
    }

    private Cell createBodyCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(10))
                .setPadding(8);
    }
}