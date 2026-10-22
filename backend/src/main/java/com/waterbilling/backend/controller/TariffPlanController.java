package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.TariffPlan;
import com.waterbilling.backend.service.TariffPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tariff-plans")
public class TariffPlanController {

    private final TariffPlanService tariffPlanService;

    public TariffPlanController(
            TariffPlanService tariffPlanService) {
        this.tariffPlanService = tariffPlanService;
    }

    @PostMapping
    public ResponseEntity<TariffPlan> createTariffPlan(
            @RequestBody Map<String, String> request) {

        TariffPlan tariffPlan =
                tariffPlanService.createTariffPlan(
                        request.get("name"),
                        Double.parseDouble(request.get("baseRate")),
                        Double.parseDouble(request.get("higherRate")),
                        Double.parseDouble(request.get("baseLimit"))
                );

        return ResponseEntity.ok(tariffPlan);
    }

    @GetMapping
    public ResponseEntity<List<TariffPlan>> getAllTariffPlans() {
        return ResponseEntity.ok(
                tariffPlanService.getAllTariffPlans()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TariffPlan> getTariffPlanById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                tariffPlanService.getTariffPlanById(id)
        );
    }
}
