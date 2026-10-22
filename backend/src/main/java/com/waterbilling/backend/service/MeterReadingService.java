package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.repository.MeterReadingRepository;
import com.waterbilling.backend.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final PropertyRepository propertyRepository;

    public MeterReadingService(
            MeterReadingRepository meterReadingRepository,
            PropertyRepository propertyRepository) {
        this.meterReadingRepository = meterReadingRepository;
        this.propertyRepository = propertyRepository;
    }

    public MeterReading addMeterReading(
            Long propertyId,
            Double previousReading,
            Double currentReading,
            LocalDate readingDate) {

        if (previousReading == null || currentReading == null) {
            throw new RuntimeException("Readings cannot be null");
        }

        if (currentReading < previousReading) {
            throw new RuntimeException(
                    "Current reading cannot be less than previous reading");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        MeterReading reading = new MeterReading();
        reading.setProperty(property);
        reading.setPreviousReading(previousReading);
        reading.setCurrentReading(currentReading);
        reading.setReadingDate(readingDate);
        reading.setUnitsConsumed(currentReading - previousReading);

        return meterReadingRepository.save(reading);
    }

    public List<MeterReading> getAllMeterReadings() {
        return meterReadingRepository.findAll();
    }

    public MeterReading getMeterReadingById(Long id) {
        return meterReadingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meter reading not found"));
    }

    public List<MeterReading> getMeterReadingsByProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        return meterReadingRepository.findByProperty(property);
    }

    public void deleteMeterReading(Long id) {
        if (!meterReadingRepository.existsById(id)) {
            throw new RuntimeException("Meter reading not found");
        }

        meterReadingRepository.deleteById(id);
    }
}
