package com.waterbilling.backend.repository;

import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MeterReadingRepository
        extends JpaRepository<MeterReading, Long> {

    List<MeterReading> findByProperty(Property property);

    List<MeterReading> findByReadingDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );
}