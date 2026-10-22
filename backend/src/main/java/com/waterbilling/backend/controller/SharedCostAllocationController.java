package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.SharedCostAllocation;
import com.waterbilling.backend.service.SharedCostAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shared-cost")
public class SharedCostAllocationController {

    @Autowired
    private SharedCostAllocationService allocationService;

    @PostMapping("/generate/{billingCycleId}")
    public ResponseEntity<?> generateAllocations(@PathVariable Long billingCycleId) {
        try {
            allocationService.generateAllocations(billingCycleId);
            return ResponseEntity.ok(Map.of("message", "Allocations generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/billing-cycle/{billingCycleId}")
    public ResponseEntity<List<SharedCostAllocation>> getAllocationsByBillingCycle(@PathVariable Long billingCycleId) {
        List<SharedCostAllocation> allocations = allocationService.getAllocationsByBillingCycle(billingCycleId);
        return ResponseEntity.ok(allocations);
    }
}
