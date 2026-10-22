package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    // Get all properties
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // Get property by ID
    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    // Save a new property or update existing
    public Property saveProperty(Property property) {
        return propertyRepository.save(property);
    }

    // Update property details
    public Property updateProperty(Long id, Property propertyDetails) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        property.setPropertyNo(propertyDetails.getPropertyNo());
        property.setAddress(propertyDetails.getAddress());
        property.setMeterNumber(propertyDetails.getMeterNumber());
        property.setFlatSize(propertyDetails.getFlatSize());
        property.setOccupancy(propertyDetails.getOccupancy());
        property.setResident(propertyDetails.getResident());

        return propertyRepository.save(property);
    }

    // Delete property
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}