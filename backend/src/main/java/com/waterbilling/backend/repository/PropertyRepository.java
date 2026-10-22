package com.waterbilling.backend.repository;

import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    Optional<Property> findByPropertyNo(String propertyNo);

    Optional<Property> findByMeterNumber(String meterNumber);

    List<Property> findByResident(Resident resident);
}
