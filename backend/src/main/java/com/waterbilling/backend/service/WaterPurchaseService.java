package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.BillingCycle;
import com.waterbilling.backend.entity.WaterPurchase;
import com.waterbilling.backend.repository.BillingCycleRepository;
import com.waterbilling.backend.repository.WaterPurchaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WaterPurchaseService {

    private final WaterPurchaseRepository waterPurchaseRepository;
    private final BillingCycleRepository billingCycleRepository;

    public WaterPurchaseService(
            WaterPurchaseRepository waterPurchaseRepository,
            BillingCycleRepository billingCycleRepository) {

        this.waterPurchaseRepository = waterPurchaseRepository;
        this.billingCycleRepository = billingCycleRepository;
    }

    public WaterPurchase createWaterPurchase(
            String source,
            Double purchasedVolume,
            Double unitCost,
            LocalDate purchaseDate) {

        return createWaterPurchase(
                source,
                purchasedVolume,
                unitCost,
                purchaseDate,
                null
        );
    }

    public WaterPurchase createWaterPurchase(
            String source,
            Double purchasedVolume,
            Double unitCost,
            LocalDate purchaseDate,
            Long billingCycleId) {

        if (source == null || source.isBlank()) {
            throw new RuntimeException(
                    "Water purchase source is required");
        }

        if (purchasedVolume == null || purchasedVolume <= 0) {
            throw new RuntimeException(
                    "Purchased volume must be greater than zero");
        }

        if (unitCost == null || unitCost < 0) {
            throw new RuntimeException(
                    "Unit cost cannot be negative");
        }

        if (purchaseDate == null) {
            throw new RuntimeException(
                    "Purchase date is required");
        }

        BillingCycle billingCycle = null;

        if (billingCycleId != null) {
            billingCycle = billingCycleRepository.findById(billingCycleId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Billing cycle not found"));

            if (!"OPEN".equals(billingCycle.getStatus())) {
                throw new RuntimeException(
                        "Water purchase can only be added to an OPEN billing cycle");
            }
        }

        double totalCost =
                purchasedVolume * unitCost;

        WaterPurchase waterPurchase =
                new WaterPurchase();

        waterPurchase.setBillingCycle(billingCycle);
        waterPurchase.setSource(source);
        waterPurchase.setPurchasedVolume(purchasedVolume);
        waterPurchase.setUnitCost(unitCost);
        waterPurchase.setTotalCost(totalCost);
        waterPurchase.setPurchaseDate(purchaseDate);

        return waterPurchaseRepository.save(
                waterPurchase
        );
    }

    public List<WaterPurchase> getAllWaterPurchases() {
        return waterPurchaseRepository.findAll();
    }

    public WaterPurchase getWaterPurchaseById(Long id) {

        return waterPurchaseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Water purchase not found"));
    }

    public void deleteWaterPurchase(Long id) {

        if (!waterPurchaseRepository.existsById(id)) {
            throw new RuntimeException(
                    "Water purchase not found");
        }

        waterPurchaseRepository.deleteById(id);
    }
}