package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.repository.BillRepository;
import com.waterbilling.backend.service.InvoicePdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoicePdfService invoicePdfService;

    @Autowired
    private BillRepository billRepository;

    // =========================
    // DOWNLOAD PDF INVOICE
    // =========================
    @GetMapping("/{billId}/pdf")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long billId) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));

        byte[] pdfBytes = invoicePdfService.generateInvoicePdf(bill);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "invoice-" + bill.getBillNumber() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
