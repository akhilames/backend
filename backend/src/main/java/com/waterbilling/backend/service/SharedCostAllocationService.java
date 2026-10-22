package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.BillingCycle;
import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.entity.SharedCostAllocation;
import com.waterbilling.backend.entity.WaterPurchase;
import com.waterbilling.backend.repository.BillingCycleRepository;
import com.waterbilling.backend.repository.PropertyRepository;
import com.waterbilling.backend.repository.SharedCostAllocationRepository;
import com.waterbilling.backend.repository.WaterPurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SharedCostAllocationService {

    @Autowired private SharedCostAllocationRepository allocationRepository;
    @Autowired private BillingCycleRepository cycleRepository;
    @Autowired private WaterPurchaseRepository purchaseRepository;
    @Autowired private PropertyRepository propertyRepository;

    public void generateAllocations(Long cycleId) {
        BillingCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new RuntimeException("Cycle not found"));

        List<WaterPurchase> allPurchases = purchaseRepository.findAll();
        List<WaterPurchase> purchases = allPurchases.stream()
                .filter(p -> p.getBillingCycle() != null && p.getBillingCycle().getId().equals(cycleId))
                .collect(Collectors.toList());

        double totalCost = purchases.stream().mapToDouble(WaterPurchase::getTotalCost).sum();

        List<Property> properties = propertyRepository.findAll();
        if (properties.isEmpty()) return;

        double totalFlatSize = properties.stream().mapToDouble(p -> p.getFlatSize() != null ? p.getFlatSize() : 0).sum();

        for (Property property : properties) {
            double share = 0;
            if (totalFlatSize > 0) {
                share = (property.getFlatSize() != null ? property.getFlatSize() : 0) / totalFlatSize * totalCost;
            } else {
                share = totalCost / properties.size();
            }

            SharedCostAllocation allocation = new SharedCostAllocation();
            allocation.setBillingCycle(cycle);
            allocation.setProperty(property);
            allocation.setAllocationAmount(share);
            allocation.setAllocationMethod("FLAT_SIZE");

            allocationRepository.save(allocation);
        }
    }

    public List<SharedCostAllocation> getAllocationsByBillingCycle(Long cycleId) {
        return allocationRepository.findAll().stream()
                .filter(a -> a.getBillingCycle() != null && a.getBillingCycle().getId().equals(cycleId))
                .collect(Collectors.toList());
    }
}