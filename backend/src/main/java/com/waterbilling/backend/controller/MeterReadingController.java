package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.repository.MeterReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meter-readings")
public class MeterReadingController {

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    @GetMapping
    public List<Map<String, Object>> getAllReadings() {
        List<MeterReading> readings = meterReadingRepository.findAll();

        return readings.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("readingDate", r.getReadingDate());
            map.put("previousReading", r.getPreviousReading());
            map.put("currentReading", r.getCurrentReading());
            map.put("unitsConsumed", r.getUnitsConsumed());

            if (r.getProperty() != null) {
                map.put("property", Map.of("propertyNo", r.getProperty().getPropertyNo()));
            } else {
                map.put("property", null);
            }
            return map;
        }).collect(Collectors.toList());
    }
}