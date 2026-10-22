package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.BillingCycle;
import com.waterbilling.backend.service.BillingCycleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing-cycles")
public class BillingCycleController {

    private final BillingCycleService billingCycleService;

    public BillingCycleController(
            BillingCycleService billingCycleService) {

        this.billingCycleService = billingCycleService;
    }

    @PostMapping
    public ResponseEntity<BillingCycle> createBillingCycle(
            @RequestBody Map<String, String> request) {

        BillingCycle billingCycle =
                billingCycleService.createBillingCycle(
                        request.get("cycleName"),
                        LocalDate.parse(request.get("startDate")),
                        LocalDate.parse(request.get("endDate"))
                );

        return ResponseEntity.ok(billingCycle);
    }

    @GetMapping
    public ResponseEntity<List<BillingCycle>> getAllBillingCycles() {

        return ResponseEntity.ok(
                billingCycleService.getAllBillingCycles()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingCycle> getBillingCycleById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                billingCycleService.getBillingCycleById(id)
        );
    }

    @PostMapping("/{id}/finalize")
    public ResponseEntity<BillingCycle> finalizeBillingCycle(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                billingCycleService.finalizeBillingCycle(id)
        );
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<BillingCycle> archiveBillingCycle(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                billingCycleService.archiveBillingCycle(id)
        );
    }
}