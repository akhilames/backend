
package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.BillingCycle;
import com.waterbilling.backend.repository.BillingCycleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillingCycleService {

    private final BillingCycleRepository billingCycleRepository;

    public BillingCycleService(
            BillingCycleRepository billingCycleRepository) {

        this.billingCycleRepository = billingCycleRepository;
    }

    public BillingCycle createBillingCycle(
            String cycleName,
            LocalDate startDate,
            LocalDate endDate) {

        if (cycleName == null || cycleName.isBlank()) {
            throw new RuntimeException(
                    "Billing cycle name is required");
        }

        if (startDate == null || endDate == null) {
            throw new RuntimeException(
                    "Start date and end date are required");
        }

        if (endDate.isBefore(startDate)) {
            throw new RuntimeException(
                    "End date cannot be before start date");
        }

        BillingCycle billingCycle = new BillingCycle();

        billingCycle.setCycleName(cycleName);
        billingCycle.setStartDate(startDate);
        billingCycle.setEndDate(endDate);
        billingCycle.setStatus("OPEN");

        return billingCycleRepository.save(billingCycle);
    }

    public List<BillingCycle> getAllBillingCycles() {
        return billingCycleRepository.findAll();
    }

    public BillingCycle getBillingCycleById(Long id) {

        return billingCycleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Billing cycle not found"));
    }

    public BillingCycle finalizeBillingCycle(Long id) {

        BillingCycle billingCycle =
                getBillingCycleById(id);

        if (!"OPEN".equals(billingCycle.getStatus())) {
            throw new RuntimeException(
                    "Only OPEN billing cycles can be finalized");
        }

        billingCycle.setStatus("FINALIZED");

        return billingCycleRepository.save(billingCycle);
    }

    public BillingCycle archiveBillingCycle(Long id) {

        BillingCycle billingCycle =
                getBillingCycleById(id);

        if (!"FINALIZED".equals(billingCycle.getStatus())) {
            throw new RuntimeException(
                    "Only FINALIZED billing cycles can be archived");
        }

        billingCycle.setStatus("ARCHIVED");

        return billingCycleRepository.save(billingCycle);
    }
}