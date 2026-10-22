package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.WaterPurchase;
import com.waterbilling.backend.service.WaterPurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/water-purchases")
public class WaterPurchaseController {

    private final WaterPurchaseService waterPurchaseService;

    public WaterPurchaseController(
            WaterPurchaseService waterPurchaseService) {

        this.waterPurchaseService = waterPurchaseService;
    }

    @PostMapping
    public ResponseEntity<WaterPurchase> createWaterPurchase(
            @RequestBody Map<String, String> request) {

        Long billingCycleId = null;

        if (request.get("billingCycleId") != null
                && !request.get("billingCycleId").isBlank()) {

            billingCycleId = Long.parseLong(
                    request.get("billingCycleId")
            );
        }

        WaterPurchase waterPurchase =
                waterPurchaseService.createWaterPurchase(
                        request.get("source"),
                        Double.parseDouble(
                                request.get("purchasedVolume")
                        ),
                        Double.parseDouble(
                                request.get("unitCost")
                        ),
                        LocalDate.parse(
                                request.get("purchaseDate")
                        ),
                        billingCycleId
                );

        return ResponseEntity.ok(waterPurchase);
    }

    @GetMapping
    public ResponseEntity<List<WaterPurchase>> getAllWaterPurchases() {

        return ResponseEntity.ok(
                waterPurchaseService.getAllWaterPurchases()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaterPurchase> getWaterPurchaseById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                waterPurchaseService.getWaterPurchaseById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWaterPurchase(
            @PathVariable Long id) {

        waterPurchaseService.deleteWaterPurchase(id);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Water purchase deleted successfully"
                )
        );
    }
}