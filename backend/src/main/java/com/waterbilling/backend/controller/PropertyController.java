package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(
            @RequestBody Map<String, String> request) {

        Double flatSize = null;
        Integer occupancy = null;

        if (request.get("flatSize") != null
                && !request.get("flatSize").isBlank()) {

            flatSize = Double.parseDouble(
                    request.get("flatSize")
            );
        }

        if (request.get("occupancy") != null
                && !request.get("occupancy").isBlank()) {

            occupancy = Integer.parseInt(
                    request.get("occupancy")
            );
        }

        Property property = propertyService.createProperty(
                request.get("propertyNo"),
                request.get("address"),
                request.get("meterNumber"),
                Long.parseLong(request.get("residentId")),
                flatSize,
                occupancy
        );

        return ResponseEntity.ok(property);
    }

    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(
                propertyService.getAllProperties()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                propertyService.getPropertyById(id)
        );
    }

    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<Property>> getPropertiesByResident(
            @PathVariable Long residentId) {

        return ResponseEntity.ok(
                propertyService.getPropertiesByResident(residentId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProperty(
            @PathVariable Long id) {

        propertyService.deleteProperty(id);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Property deleted successfully"
                )
        );
    }
}