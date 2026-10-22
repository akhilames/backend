package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.BillingCycle;
import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.entity.SharedCostAllocation;
import com.waterbilling.backend.entity.WaterPurchase;
import com.waterbilling.backend.repository.BillingCycleRepository;
import com.waterbilling.backend.repository.MeterReadingRepository;
import com.waterbilling.backend.repository.PropertyRepository;
import com.waterbilling.backend.repository.SharedCostAllocationRepository;
import com.waterbilling.backend.repository.WaterPurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SharedCostAllocationService {

    private final BillingCycleRepository billingCycleRepository;
    private final WaterPurchaseRepository waterPurchaseRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final PropertyRepository propertyRepository;
    private final SharedCostAllocationRepository allocationRepository;

    public SharedCostAllocationService(
            BillingCycleRepository billingCycleRepository,
            WaterPurchaseRepository waterPurchaseRepository,
            MeterReadingRepository meterReadingRepository,
            PropertyRepository propertyRepository,
            SharedCostAllocationRepository allocationRepository) {

        this.billingCycleRepository = billingCycleRepository;
        this.waterPurchaseRepository = waterPurchaseRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.propertyRepository = propertyRepository;
        this.allocationRepository = allocationRepository;
    }

    public List<SharedCostAllocation> generateAllocations(
            Long billingCycleId) {

        BillingCycle billingCycle =
                billingCycleRepository.findById(billingCycleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Billing cycle not found"));

        if (!"FINALIZED".equals(billingCycle.getStatus())
                && !"ARCHIVED".equals(billingCycle.getStatus())) {

            throw new RuntimeException(
                    "Billing cycle must be FINALIZED before generating shared cost allocations");
        }

        List<SharedCostAllocation> existing =
                allocationRepository.findByBillingCycle(billingCycle);

        if (!existing.isEmpty()) {
            throw new RuntimeException(
                    "Shared cost allocations already generated for this billing cycle");
        }

        List<WaterPurchase> purchases =
                waterPurchaseRepository.findByBillingCycle(billingCycle);

        if (purchases.isEmpty()) {
            throw new RuntimeException(
                    "No water purchases found for this billing cycle");
        }

        double totalWaterCost = purchases.stream()
                .mapToDouble(WaterPurchase::getTotalCost)
                .sum();

        if (totalWaterCost <= 0) {
            throw new RuntimeException(
                    "Total water purchase cost must be greater than zero");
        }

        List<MeterReading> readings =
                meterReadingRepository.findByReadingDateBetween(
                        billingCycle.getStartDate(),
                        billingCycle.getEndDate()
                );

        List<Property> properties =
                propertyRepository.findAll();

        if (properties.isEmpty()) {
            throw new RuntimeException(
                    "No properties found");
        }

        List<PropertyWeight> weights =
                new ArrayList<>();

        for (Property property : properties) {

            double meterUnits = readings.stream()
                    .filter(reading ->
                            reading.getProperty() != null
                                    && reading.getProperty()
                                    .getId()
                                    .equals(property.getId()))
                    .mapToDouble(MeterReading::getUnitsConsumed)
                    .sum();

            if (meterUnits > 0) {

                weights.add(
                        new PropertyWeight(
                                property,
                                meterUnits,
                                "METER_CONSUMPTION"
                        )
                );

            } else if (property.getFlatSize() != null
                    && property.getFlatSize() > 0) {

                weights.add(
                        new PropertyWeight(
                                property,
                                property.getFlatSize(),
                                "FLAT_SIZE"
                        )
                );

            } else if (property.getOccupancy() != null
                    && property.getOccupancy() > 0) {

                weights.add(
                        new PropertyWeight(
                                property,
                                property.getOccupancy(),
                                "OCCUPANCY"
                        )
                );
            }
        }

        if (weights.isEmpty()) {
            throw new RuntimeException(
                    "No valid properties available for cost allocation");
        }

        double totalWeight = weights.stream()
                .mapToDouble(PropertyWeight::weight)
                .sum();

        List<SharedCostAllocation> allocations =
                new ArrayList<>();

        for (PropertyWeight propertyWeight : weights) {

            double allocationAmount =
                    totalWaterCost
                            * propertyWeight.weight()
                            / totalWeight;

            SharedCostAllocation allocation =
                    new SharedCostAllocation();

            allocation.setBillingCycle(billingCycle);
            allocation.setProperty(propertyWeight.property());
            allocation.setAllocationAmount(allocationAmount);
            allocation.setAllocationMethod(
                    propertyWeight.method()
            );

            allocations.add(allocation);
        }

        return allocationRepository.saveAll(allocations);
    }

    public List<SharedCostAllocation> getAllocationsByBillingCycle(
            Long billingCycleId) {

        BillingCycle billingCycle =
                billingCycleRepository.findById(billingCycleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Billing cycle not found"));

        return allocationRepository.findByBillingCycle(
                billingCycle
        );
    }

    private record PropertyWeight(
            Property property,
            double weight,
            String method) {
    }
}
