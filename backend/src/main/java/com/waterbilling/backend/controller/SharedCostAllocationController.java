package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.SharedCostAllocation;
import com.waterbilling.backend.service.SharedCostAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shared-cost-allocations")
public class SharedCostAllocationController {

    private final SharedCostAllocationService allocationService;

    public SharedCostAllocationController(
            SharedCostAllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @PostMapping("/generate/{billingCycleId}")
    public ResponseEntity<List<SharedCostAllocation>> generateAllocations(
            @PathVariable Long billingCycleId) {

        return ResponseEntity.ok(
                allocationService.generateAllocations(
                        billingCycleId
                )
        );
    }

    @GetMapping("/billing-cycle/{billingCycleId}")
    public ResponseEntity<List<SharedCostAllocation>>
    getAllocationsByBillingCycle(
            @PathVariable Long billingCycleId) {

        return ResponseEntity.ok(
                allocationService.getAllocationsByBillingCycle(
                        billingCycleId
                )
        );
    }
}
