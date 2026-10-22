package com.waterbilling.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "meters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String meterNumber;

    @Column(nullable = false)
    private LocalDate installationDate;

    @Column(nullable = false)
    private Double initialReading;

    @Column(nullable = false)
    private Double currentReading;

    @Column(nullable = false)
    private String status;   // ACTIVE, INACTIVE, FAULTY

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;
}