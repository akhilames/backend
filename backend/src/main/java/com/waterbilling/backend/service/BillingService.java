package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.entity.Resident;
import com.waterbilling.backend.repository.BillRepository;
import com.waterbilling.backend.repository.MeterReadingRepository;
import com.waterbilling.backend.repository.PropertyRepository;
import com.waterbilling.backend.repository.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class BillingService {

    @Autowired private BillRepository billRepository;
    @Autowired private PropertyRepository propertyRepository;
    @Autowired private MeterReadingRepository meterReadingRepository;
    @Autowired private ResidentRepository residentRepository; // ADDED

    public Bill generateBill(Long propertyId, Long readingId, String month, LocalDate billDate, LocalDate dueDate) {

        // 1. Property vethuku. Lekapote create chey. Kani ikkada Resident kuda assign cheyali.
        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null) {
            property = new Property();
            property.setPropertyNo("P-AUTO-" + propertyId);
            property.setAddress("Auto Address");
            property.setMeterNumber("MTR-AUTO-" + propertyId);
            property.setFlatSize(1000.0);
            property.setOccupancy(2);

            // Default Resident theesukuni assign cheddam
            Resident defaultResident = residentRepository.findById(1L).orElse(null);
            if (defaultResident == null) {
                defaultResident = new Resident();
                defaultResident.setName("Dummy Resident");
                defaultResident.setUsername("dummy" + System.currentTimeMillis());
                defaultResident.setPassword("dummy");
                defaultResident.setEmail("dummy" + System.currentTimeMillis() + "@test.com");
                defaultResident = residentRepository.save(defaultResident);
            }
            property.setResident(defaultResident);
            property = propertyRepository.save(property);
        }

        // 2. Meter Reading vethuku. Lekapote create chey.
        MeterReading reading = meterReadingRepository.findById(readingId).orElse(null);
        if (reading == null) {
            reading = new MeterReading();
            reading.setProperty(property);
            reading.setReadingDate(LocalDate.now());
            reading.setPreviousReading(0.0);
            reading.setCurrentReading(150.0);
            reading.setUnitsConsumed(150.0);
            reading = meterReadingRepository.save(reading);
        }

        double units = reading.getUnitsConsumed();
        double waterCharges = units * 10.0;
        double fixedCharges = 100.0;

        // 3. Bill generate chey
        Bill bill = new Bill();
        bill.setProperty(property);
        bill.setMeterReading(reading);
        bill.setBillingMonth(month);
        bill.setBillDate(billDate);
        bill.setDueDate(dueDate);
        bill.setBillNumber("BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bill.setWaterCharges(waterCharges);
        bill.setFixedCharges(fixedCharges);
        bill.setSharedCost(0.0);
        bill.setTotalAmount(waterCharges + fixedCharges);
        bill.setStatus("UNPAID");

        return billRepository.save(bill);
    }
}