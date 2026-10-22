package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.repository.BillRepository;
import com.waterbilling.backend.repository.MeterReadingRepository;
import com.waterbilling.backend.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class BillingService {

    @Autowired private BillRepository billRepository;
    @Autowired private PropertyRepository propertyRepository;
    @Autowired private MeterReadingRepository meterReadingRepository;

    public Bill generateBill(Long propertyId, Long readingId, String month, LocalDate billDate, LocalDate dueDate) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        MeterReading reading = meterReadingRepository.findById(readingId)
                .orElseThrow(() -> new RuntimeException("Meter Reading not found"));

        double units = reading.getUnitsConsumed();
        double waterCharges = units * 10.0;
        double fixedCharges = 100.0;

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