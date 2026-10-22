package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.service.MeterReadingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meter-readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    @PostMapping
    public ResponseEntity<MeterReading> addMeterReading(
            @RequestBody Map<String, String> request) {

        MeterReading reading = meterReadingService.addMeterReading(
                Long.parseLong(request.get("propertyId")),
                Double.parseDouble(request.get("previousReading")),
                Double.parseDouble(request.get("currentReading")),
                LocalDate.parse(request.get("readingDate"))
        );

        return ResponseEntity.ok(reading);
    }

    @GetMapping
    public ResponseEntity<List<MeterReading>> getAllMeterReadings() {
        return ResponseEntity.ok(
                meterReadingService.getAllMeterReadings()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeterReading> getMeterReadingById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                meterReadingService.getMeterReadingById(id)
        );
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<MeterReading>> getByProperty(
            @PathVariable Long propertyId) {

        return ResponseEntity.ok(
                meterReadingService.getMeterReadingsByProperty(propertyId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeterReading(
            @PathVariable Long id) {

        meterReadingService.deleteMeterReading(id);

        return ResponseEntity.ok(
                Map.of("message", "Meter reading deleted successfully")
        );
    }
}