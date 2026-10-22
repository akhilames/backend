package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.repository.BillRepository;
import com.waterbilling.backend.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired private BillRepository billRepository;
    @Autowired private BillingService billingService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllBills() {
        List<Bill> bills = billRepository.findAll();
        List<Map<String, Object>> response = bills.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("billNumber", b.getBillNumber());
            map.put("billingMonth", b.getBillingMonth());
            map.put("totalAmount", b.getTotalAmount());
            map.put("status", b.getStatus());
            if (b.getProperty() != null) map.put("property", Map.of("propertyNo", b.getProperty().getPropertyNo()));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateBill(@RequestBody Map<String, String> request) {
        try {
            Long propertyId = Long.parseLong(request.get("propertyId"));
            Long readingId = Long.parseLong(request.get("readingId"));
            String month = request.get("month");
            LocalDate billDate = LocalDate.parse(request.get("billDate"));
            LocalDate dueDate = LocalDate.parse(request.get("dueDate"));
            Bill bill = billingService.generateBill(propertyId, readingId, month, billDate, dueDate);
            return ResponseEntity.ok(Map.of("message", "Bill generated successfully", "billId", bill.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed: " + e.getMessage()));
        }
    }

    // NEW: DELETE BILL
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBill(@PathVariable Long id) {
        billRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Bill deleted successfully"));
    }
}