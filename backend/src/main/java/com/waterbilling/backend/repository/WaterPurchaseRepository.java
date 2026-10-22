package com.waterbilling.backend.repository;

import com.waterbilling.backend.entity.BillingCycle;
import com.waterbilling.backend.entity.WaterPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaterPurchaseRepository
        extends JpaRepository<WaterPurchase, Long> {

    List<WaterPurchase> findByBillingCycle(BillingCycle billingCycle);
}