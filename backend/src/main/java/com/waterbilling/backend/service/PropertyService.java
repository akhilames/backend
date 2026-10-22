package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.entity.Resident;
import com.waterbilling.backend.repository.PropertyRepository;
import com.waterbilling.backend.repository.ResidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final ResidentRepository residentRepository;

    public PropertyService(
            PropertyRepository propertyRepository,
            ResidentRepository residentRepository) {
        this.propertyRepository = propertyRepository;
        this.residentRepository = residentRepository;
    }

    public Property createProperty(
            String propertyNo,
            String address,
            String meterNumber,
            Long residentId) {

        return createProperty(
                propertyNo,
                address,
                meterNumber,
                residentId,
                null,
                null
        );
    }

    public Property createProperty(
            String propertyNo,
            String address,
            String meterNumber,
            Long residentId,
            Double flatSize,
            Integer occupancy) {

        if (propertyRepository.findByPropertyNo(propertyNo).isPresent()) {
            throw new RuntimeException("Property number already exists");
        }

        if (propertyRepository.findByMeterNumber(meterNumber).isPresent()) {
            throw new RuntimeException("Meter number already exists");
        }

        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));

        if (flatSize != null && flatSize <= 0) {
            throw new RuntimeException("Flat size must be greater than zero");
        }

        if (occupancy != null && occupancy <= 0) {
            throw new RuntimeException("Occupancy must be greater than zero");
        }

        Property property = new Property();

        property.setPropertyNo(propertyNo);
        property.setAddress(address);
        property.setMeterNumber(meterNumber);
        property.setResident(resident);
        property.setFlatSize(flatSize);
        property.setOccupancy(occupancy);

        return propertyRepository.save(property);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

    public List<Property> getPropertiesByResident(Long residentId) {

        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));

        return propertyRepository.findByResident(resident);
    }

    public void deleteProperty(Long id) {

        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Property not found");
        }

        propertyRepository.deleteById(id);
    }
}