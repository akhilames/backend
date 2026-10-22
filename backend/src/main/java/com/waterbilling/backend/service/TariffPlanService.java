
package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.TariffPlan;
import com.waterbilling.backend.repository.TariffPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TariffPlanService {

    private final TariffPlanRepository tariffPlanRepository;

    public TariffPlanService(
            TariffPlanRepository tariffPlanRepository) {
        this.tariffPlanRepository = tariffPlanRepository;
    }

    public TariffPlan createTariffPlan(
            String name,
            Double baseRate,
            Double higherRate,
            Double baseLimit) {

        if (baseRate == null || baseRate < 0) {
            throw new RuntimeException(
                    "Base rate cannot be negative");
        }

        if (higherRate == null || higherRate < 0) {
            throw new RuntimeException(
                    "Higher rate cannot be negative");
        }

        if (baseLimit == null || baseLimit <= 0) {
            throw new RuntimeException(
                    "Base limit must be greater than zero");
        }

        TariffPlan tariffPlan = new TariffPlan();

        tariffPlan.setName(name);
        tariffPlan.setBaseRate(baseRate);
        tariffPlan.setHigherRate(higherRate);
        tariffPlan.setBaseLimit(baseLimit);

        return tariffPlanRepository.save(tariffPlan);
    }

    public List<TariffPlan> getAllTariffPlans() {
        return tariffPlanRepository.findAll();
    }

    public TariffPlan getTariffPlanById(Long id) {
        return tariffPlanRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Tariff plan not found"));
    }
}