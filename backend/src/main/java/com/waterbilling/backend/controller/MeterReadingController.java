package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.repository.MeterReadingRepository;
import com.waterbilling.backend.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meter-readings")
public class MeterReadingController {

    @Autowired private MeterReadingRepository meterReadingRepository;
    @Autowired private PropertyRepository propertyRepository;

    @GetMapping
    public List<Map<String, Object>> getAllReadings() {
        return meterReadingRepository.findAll().stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("readingDate", r.getReadingDate());
            map.put("previousReading", r.getPreviousReading());
            map.put("currentReading", r.getCurrentReading());
            map.put("unitsConsumed", r.getUnitsConsumed());
            if (r.getProperty() != null) map.put("property", Map.of("propertyNo", r.getProperty().getPropertyNo()));
            return map;
        }).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createReading(@RequestBody Map<String, String> request) {
        Property property = propertyRepository.findById(Long.parseLong(request.get("propertyId"))).orElseThrow();
        MeterReading reading = new MeterReading();
        reading.setProperty(property);
        reading.setPreviousReading(Double.parseDouble(request.get("previousReading")));
        reading.setCurrentReading(Double.parseDouble(request.get("currentReading")));
        reading.setReadingDate(LocalDate.parse(request.get("readingDate")));
        reading.setUnitsConsumed(reading.getCurrentReading() - reading.getPreviousReading());
        meterReadingRepository.save(reading);
        return ResponseEntity.ok(Map.of("message", "Reading added"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReading(@PathVariable Long id, @RequestBody Map<String, String> request) {
        MeterReading reading = meterReadingRepository.findById(id).orElseThrow();
        reading.setPreviousReading(Double.parseDouble(request.get("previousReading")));
        reading.setCurrentReading(Double.parseDouble(request.get("currentReading")));
        reading.setUnitsConsumed(reading.getCurrentReading() - reading.getPreviousReading());
        meterReadingRepository.save(reading);
        return ResponseEntity.ok(Map.of("message", "Reading updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReading(@PathVariable Long id) {
        meterReadingRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Reading deleted"));
    }
}