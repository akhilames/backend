package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Meter;
import com.waterbilling.backend.repository.MeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeterService {

    @Autowired
    private MeterRepository meterRepository;

    // Get all meters
    public List<Meter> getAllMeters() {
        return meterRepository.findAll();
    }

    // Get meter by ID
    public Optional<Meter> getMeterById(Long id) {
        return meterRepository.findById(id);
    }

    // Get meter by meter number
    public Meter getMeterByNumber(String meterNumber) {
        return meterRepository.findAll().stream()
                .filter(m -> m.getMeterNumber().equals(meterNumber))
                .findFirst()
                .orElse(null);
    }

    // Create / Save meter
    public Meter saveMeter(Meter meter) {
        if (meterRepository.existsByMeterNumber(meter.getMeterNumber())) {
            throw new RuntimeException("Meter number already exists: " + meter.getMeterNumber());
        }
        return meterRepository.save(meter);
    }

    // Update meter
    public Meter updateMeter(Long id, Meter updatedMeter) {
        Meter existing = meterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meter not found with id: " + id));

        existing.setMeterNumber(updatedMeter.getMeterNumber());
        existing.setInstallationDate(updatedMeter.getInstallationDate());
        existing.setInitialReading(updatedMeter.getInitialReading());
        existing.setCurrentReading(updatedMeter.getCurrentReading());
        existing.setStatus(updatedMeter.getStatus());
        existing.setResident(updatedMeter.getResident());

        return meterRepository.save(existing);
    }

    // Delete meter
    public void deleteMeter(Long id) {
        if (!meterRepository.existsById(id)) {
            throw new RuntimeException("Meter not found with id: " + id);
        }
        meterRepository.deleteById(id);
    }

    // Check if meter exists by number
    public boolean existsByMeterNumber(String meterNumber) {
        return meterRepository.existsByMeterNumber(meterNumber);
    }
}