package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.entity.BillingCycle;
import com.waterbilling.backend.entity.MeterReading;
import com.waterbilling.backend.entity.Property;
import com.waterbilling.backend.entity.SharedCostAllocation;
import com.waterbilling.backend.entity.TariffPlan;
import com.waterbilling.backend.repository.BillRepository;
import com.waterbilling.backend.repository.BillingCycleRepository;
import com.waterbilling.backend.repository.MeterReadingRepository;
import com.waterbilling.backend.repository.PropertyRepository;
import com.waterbilling.backend.repository.SharedCostAllocationRepository;
import com.waterbilling.backend.repository.TariffPlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BillingService {

    private static final double FIXED_CHARGES = 100.0;
    private static final Long DEFAULT_TARIFF_PLAN_ID = 1L;

    private final BillRepository billRepository;
    private final PropertyRepository propertyRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final TariffPlanRepository tariffPlanRepository;
    private final BillingCycleRepository billingCycleRepository;
    private final SharedCostAllocationRepository sharedCostAllocationRepository;

    public BillingService(
            BillRepository billRepository,
            PropertyRepository propertyRepository,
            MeterReadingRepository meterReadingRepository,
            TariffPlanRepository tariffPlanRepository,
            BillingCycleRepository billingCycleRepository,
            SharedCostAllocationRepository sharedCostAllocationRepository) {

        this.billRepository = billRepository;
        this.propertyRepository = propertyRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.tariffPlanRepository = tariffPlanRepository;
        this.billingCycleRepository = billingCycleRepository;
        this.sharedCostAllocationRepository =
                sharedCostAllocationRepository;
    }

    public Bill generateBill(
            Long propertyId,
            Long meterReadingId,
            String billingMonth,
            LocalDate billDate,
            LocalDate dueDate) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() ->
                        new RuntimeException("Property not found"));

        MeterReading meterReading =
                meterReadingRepository.findById(meterReadingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Meter reading not found"));

        if (meterReading.getProperty() == null
                || !meterReading.getProperty()
                .getId()
                .equals(propertyId)) {

            throw new RuntimeException(
                    "Meter reading does not belong to this property");
        }

        /*
         * Prevent duplicate bills for the same
         * property and meter reading.
         */
        List<Bill> existingBills =
                billRepository.findByPropertyAndMeterReading(
                        property,
                        meterReading
                );

        if (!existingBills.isEmpty()) {
            throw new RuntimeException(
                    "Bill already exists for this property and meter reading");
        }

        Double unitsConsumed =
                meterReading.getUnitsConsumed();

        if (unitsConsumed == null || unitsConsumed < 0) {
            throw new RuntimeException(
                    "Units consumed cannot be negative or null");
        }

        TariffPlan tariffPlan =
                tariffPlanRepository.findById(DEFAULT_TARIFF_PLAN_ID)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Tariff plan not found"));

        double waterCharges;

        if (unitsConsumed <= tariffPlan.getBaseLimit()) {

            waterCharges =
                    unitsConsumed * tariffPlan.getBaseRate();

        } else {

            double baseUnits =
                    tariffPlan.getBaseLimit();

            double higherUnits =
                    unitsConsumed - tariffPlan.getBaseLimit();

            waterCharges =
                    (baseUnits * tariffPlan.getBaseRate())
                            + (higherUnits * tariffPlan.getHigherRate());
        }

        /*
         * Find the billing cycle containing
         * the meter reading date.
         */
        BillingCycle billingCycle =
                findBillingCycleForDate(
                        meterReading.getReadingDate()
                );

        double sharedCost = 0.0;

        if (billingCycle != null) {

            List<SharedCostAllocation> allocations =
                    sharedCostAllocationRepository
                            .findByBillingCycle(billingCycle);

            sharedCost = allocations.stream()
                    .filter(allocation ->
                            allocation.getProperty() != null
                                    && allocation.getProperty()
                                    .getId()
                                    .equals(propertyId))
                    .mapToDouble(
                            SharedCostAllocation::getAllocationAmount
                    )
                    .findFirst()
                    .orElse(0.0);
        }

        double totalAmount =
                waterCharges
                        + FIXED_CHARGES
                        + sharedCost;

        Bill bill = new Bill();

        bill.setProperty(property);
        bill.setMeterReading(meterReading);

        bill.setBillNumber(
                "BILL-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase()
        );

        bill.setBillingMonth(billingMonth);
        bill.setWaterCharges(waterCharges);
        bill.setFixedCharges(FIXED_CHARGES);
        bill.setSharedCost(sharedCost);
        bill.setTotalAmount(totalAmount);
        bill.setBillDate(billDate);
        bill.setDueDate(dueDate);
        bill.setStatus("UNPAID");

        return billRepository.save(bill);
    }

    private BillingCycle findBillingCycleForDate(
            LocalDate date) {

        if (date == null) {
            return null;
        }

        return billingCycleRepository.findAll()
                .stream()
                .filter(cycle ->
                        cycle.getStartDate() != null
                                && cycle.getEndDate() != null
                                && !date.isBefore(
                                cycle.getStartDate())
                                && !date.isAfter(
                                cycle.getEndDate()))
                .findFirst()
                .orElse(null);
    }

    public void deleteBill(Long id) {

        if (!billRepository.existsById(id)) {
            throw new RuntimeException("Bill not found");
        }

        billRepository.deleteById(id);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long id) {

        return billRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bill not found"));
    }

    // Resident-specific bills
    public List<Bill> getBillsByResident(Long residentId) {

        return billRepository.findByProperty_Resident_Id(residentId);
    }

    public List<Bill> getBillsByProperty(Long propertyId) {

        Property property =
                propertyRepository.findById(propertyId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Property not found"));

        return billRepository.findByProperty(property);
    }

    public List<Bill> getBillsByStatus(String status) {

        return billRepository.findByStatus(status);
    }
}