package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillingService billingService; // <-- Ikkada BillingService vadutunnam

    @PostMapping("/generate")
    public ResponseEntity<?> generateBill(@RequestBody Map<String, String> request) {
        try {
            Long propertyId = Long.parseLong(request.get("propertyId"));
            Long readingId = Long.parseLong(request.get("readingId"));
            String month = request.get("month");

            LocalDate billDate = LocalDate.parse(request.get("billDate"));
            LocalDate dueDate = LocalDate.parse(request.get("dueDate"));

            Bill bill = billingService.generateBill(propertyId, readingId, month, billDate, dueDate);
            return ResponseEntity.ok(bill);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to generate bill: " + e.getMessage()));
        }
    }
}