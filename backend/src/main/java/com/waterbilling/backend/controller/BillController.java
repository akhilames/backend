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
        return ResponseEntity.ok(billRepository.findAll().stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("billNumber", b.getBillNumber());
            map.put("billingMonth", b.getBillingMonth());
            map.put("totalAmount", b.getTotalAmount());
            map.put("status", b.getStatus());
            if (b.getProperty() != null) map.put("property", Map.of("propertyNo", b.getProperty().getPropertyNo()));
            return map;
        }).collect(Collectors.toList()));
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateBill(@RequestBody Map<String, String> request) {
        try {
            Long propertyId = Long.parseLong(request.get("propertyId"));
            Long readingId = Long.parseLong(request.get("readingId"));
            String month = request.get("month");

            // Error fix: Date parse chesetappudu crash avvakunda handle cheyyadam
            LocalDate billDate = LocalDate.now();
            LocalDate dueDate = LocalDate.now().plusDays(10);

            if (request.get("billDate") != null && !request.get("billDate").isEmpty()) {
                billDate = LocalDate.parse(request.get("billDate"));
            }
            if (request.get("dueDate") != null && !request.get("dueDate").isEmpty()) {
                dueDate = LocalDate.parse(request.get("dueDate"));
            }

            Bill bill = billingService.generateBill(propertyId, readingId, month, billDate, dueDate);
            return ResponseEntity.ok(Map.of("message", "Bill generated successfully", "billId", bill.getId()));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBill(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Bill bill = billRepository.findById(id).orElseThrow();
            bill.setBillingMonth(request.get("billingMonth"));
            bill.setStatus(request.get("status"));
            billRepository.save(bill);
            return ResponseEntity.ok(Map.of("message", "Bill updated"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBill(@PathVariable Long id) {
        try {
            billRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Bill deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Cannot delete bill"));
        }
    }
}