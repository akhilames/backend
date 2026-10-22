package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Alert;
import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.entity.Resident;
import com.waterbilling.backend.repository.AlertRepository;
import com.waterbilling.backend.repository.BillRepository;
import com.waterbilling.backend.repository.MeterReadingRepository;
import com.waterbilling.backend.repository.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    @Autowired private EmailService emailService;
    @Autowired private ResidentRepository residentRepository;
    @Autowired private MeterReadingRepository meterReadingRepository;
    @Autowired private BillRepository billRepository;
    @Autowired private AlertRepository alertRepository;

    private static final double DAILY_THRESHOLD = 100.0;   // ← CHANGED (500 → 100)
    private static final double LEAK_MULTIPLIER = 2.0;

    public void checkAllResidentsForOveruse() {
        List<Resident> residents = residentRepository.findAll();
        System.out.println("Checking " + residents.size() + " residents...");
        for (Resident resident : residents) {
            checkOveruseForResident(resident);
        }
    }

    public void checkOveruseForResident(Resident resident) {
        if (resident == null) return;

        List<MeterReading> readings = meterReadingRepository.findAll();
        MeterReading latest = null;

        for (MeterReading r : readings) {
            if (r.getProperty() != null
                    && r.getProperty().getResident() != null
                    && r.getProperty().getResident().getId().equals(resident.getId())) {
                if (latest == null
                        || r.getReadingDate().isAfter(latest.getReadingDate())) {
                    latest = r;
                }
            }
        }

        if (latest == null) return;

        double usage = latest.getUnitsConsumed();
        System.out.println("Resident " + resident.getName() + " usage: " + usage);

        if (usage > DAILY_THRESHOLD) {
            saveAlert(resident, "OVERUSE",
                    "Water Overuse Alert",
                    "Usage " + usage + "L exceeded threshold " + DAILY_THRESHOLD + "L",
                    "HIGH");

            if (resident.getEmail() != null) {
                emailService.sendOveruseAlert(
                        resident.getEmail(), resident.getName(),
                        usage, DAILY_THRESHOLD);
            }
        }

        double average = calculateAverageUsage(resident);
        if (average > 0 && usage > average * LEAK_MULTIPLIER) {
            saveAlert(resident, "LEAK",
                    "Possible Water Leak",
                    "Usage " + usage + "L is 2x higher than average " + average + "L",
                    "HIGH");

            if (resident.getEmail() != null) {
                emailService.sendLeakAlert(
                        resident.getEmail(), resident.getName(),
                        usage, average);
            }
        }
    }

    private void saveAlert(Resident resident, String type,
                           String title, String message, String severity) {
        Alert alert = new Alert();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setType(type);
        alert.setSeverity(severity);
        alert.setStatus("UNREAD");
        alert.setResident(resident);
        alertRepository.save(alert);
        System.out.println("✅ Alert saved: " + title);
    }

    public double calculateAverageUsage(Resident resident) {
        List<MeterReading> readings = meterReadingRepository.findAll();
        double total = 0;
        int count = 0;

        for (MeterReading r : readings) {
            if (r.getProperty() != null
                    && r.getProperty().getResident() != null
                    && r.getProperty().getResident().getId().equals(resident.getId())) {
                total += r.getUnitsConsumed();
                count++;
            }
        }
        return count > 0 ? total / count : 0;
    }

    public void sendBillNotifications() {
        List<Bill> bills = billRepository.findAll();
        for (Bill bill : bills) {
            try {
                Resident resident = bill.getProperty() != null
                        ? bill.getProperty().getResident() : null;

                if (resident != null) {
                    saveAlert(resident, "BILL",
                            "New Bill Generated",
                            "Bill for " + bill.getBillingMonth()
                                    + " amount ₹" + bill.getTotalAmount(),
                            "MEDIUM");

                    if (resident.getEmail() != null) {
                        emailService.sendBillNotification(
                                resident.getEmail(), resident.getName(),
                                bill.getBillingMonth(), bill.getTotalAmount(),
                                bill.getDueDate() != null
                                        ? bill.getDueDate().toString() : "N/A");
                    }
                }
            } catch (Exception e) {
                System.err.println("Bill alert error: " + e.getMessage());
            }
        }
    }
}