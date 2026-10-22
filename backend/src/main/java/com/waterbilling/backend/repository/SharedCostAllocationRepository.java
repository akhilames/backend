package com.waterbilling.backend.repository;

import com.waterbilling.backend.entity.BillingCycle;
import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.entity.SharedCostAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedCostAllocationRepository
        extends JpaRepository<SharedCostAllocation, Long> {

    List<SharedCostAllocation> findByBillingCycle(
            BillingCycle billingCycle
    );

    List<SharedCostAllocation> findByProperty(
            Property property
    );
}
