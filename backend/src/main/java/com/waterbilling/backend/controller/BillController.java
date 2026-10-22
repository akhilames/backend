package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillingService billingService;

    public BillController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Bill> generateBill(
            @RequestBody Map<String, String> request) {

        Bill bill = billingService.generateBill(
                Long.parseLong(request.get("propertyId")),
                Long.parseLong(request.get("meterReadingId")),
                request.get("billingMonth"),
                LocalDate.parse(request.get("billDate")),
                LocalDate.parse(request.get("dueDate"))
        );

        return ResponseEntity.ok(bill);
    }

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {

        return ResponseEntity.ok(
                billingService.getAllBills()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                billingService.getBillById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBill(
            @PathVariable Long id) {

        billingService.deleteBill(id);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Bill deleted successfully"
                )
        );
    }

    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<Bill>> getBillsByResident(
            @PathVariable Long residentId) {

        return ResponseEntity.ok(
                billingService.getBillsByResident(residentId)
        );
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<Bill>> getBillsByProperty(
            @PathVariable Long propertyId) {

        return ResponseEntity.ok(
                billingService.getBillsByProperty(propertyId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Bill>> getBillsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                billingService.getBillsByStatus(status)
        );
    }
}