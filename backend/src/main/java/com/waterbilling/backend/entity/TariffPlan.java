package com.waterbilling.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tariff_plans")
public class TariffPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double baseRate;

    @Column(nullable = false)
    private Double higherRate;

    @Column(nullable = false)
    private Double baseLimit;

    public TariffPlan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(Double baseRate) {
        this.baseRate = baseRate;
    }

    public Double getHigherRate() {
        return higherRate;
    }

    public void setHigherRate(Double higherRate) {
        this.higherRate = higherRate;
    }

    public Double getBaseLimit() {
        return baseLimit;
    }

    public void setBaseLimit(Double baseLimit) {
        this.baseLimit = baseLimit;
    }
}