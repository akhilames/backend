package com.waterbilling.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String billNumber;
    private String billingMonth;
    private LocalDate billDate;
    private LocalDate dueDate;

    private Double waterCharges;
    private Double fixedCharges;
    private Double sharedCost;
    private Double totalAmount;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "bills", "meterReadings", "payments"})
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_reading_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "bills"})
    private MeterReading meterReading;
}