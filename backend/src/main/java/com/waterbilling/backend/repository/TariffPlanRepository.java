package com.waterbilling.backend.repository;

import com.waterbilling.backend.entity.TariffPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffPlanRepository
        extends JpaRepository<TariffPlan, Long> {
}