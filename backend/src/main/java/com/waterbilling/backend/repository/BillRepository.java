package com.waterbilling.backend.repository;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByBillNumber(String billNumber);

    List<Bill> findByProperty(Property property);

    List<Bill> findByStatus(String status);

    List<Bill> findByProperty_Resident_Id(Long residentId);

    List<Bill> findByPropertyAndMeterReading(
            Property property,
            MeterReading meterReading
    );
}