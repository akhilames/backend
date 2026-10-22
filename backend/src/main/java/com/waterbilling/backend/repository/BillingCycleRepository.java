package com.waterbilling.backend.repository;

import com.waterbilling.backend.entity.BillingCycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingCycleRepository
        extends JpaRepository<BillingCycle, Long> {
}
