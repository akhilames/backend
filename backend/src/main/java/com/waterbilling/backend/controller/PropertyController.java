package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.entity.Resident;
import com.waterbilling.backend.repository.PropertyRepository;
import com.waterbilling.backend.repository.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired private PropertyRepository propertyRepository;
    @Autowired private ResidentRepository residentRepository;

    @GetMapping
    public List<Property> getAllProperties() { return propertyRepository.findAll(); }

    @PostMapping
    public ResponseEntity<?> createProperty(@RequestBody Map<String, String> request) {
        Property property = new Property();
        property.setPropertyNo(request.get("propertyNo"));
        property.setAddress(request.get("address"));
        property.setMeterNumber(request.get("meterNumber"));
        property.setFlatSize(Double.parseDouble(request.get("flatSize")));
        property.setOccupancy(Integer.parseInt(request.get("occupancy")));
        if (request.get("residentId") != null && !request.get("residentId").isEmpty()) {
            Resident resident = residentRepository.findById(Long.parseLong(request.get("residentId"))).orElse(null);
            property.setResident(resident);
        }
        propertyRepository.save(property);
        return ResponseEntity.ok(Map.of("message", "Property created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProperty(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Property property = propertyRepository.findById(id).orElseThrow();
            property.setPropertyNo(request.get("propertyNo"));
            property.setAddress(request.get("address"));
            property.setFlatSize(Double.parseDouble(request.get("flatSize")));
            property.setOccupancy(Integer.parseInt(request.get("occupancy")));
            propertyRepository.save(property);
            return ResponseEntity.ok(Map.of("message", "Property updated"));
        } catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        propertyRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Property deleted"));
    }
}