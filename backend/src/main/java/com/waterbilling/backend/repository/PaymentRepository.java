package com.waterbilling.backend.repository;

import com.waterbilling.backend.entity.Payment;
import com.waterbilling.backend.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByBill(Bill bill);

    List<Payment> findByStatus(String status);

    List<Payment> findByBill_Property_Resident_Id(Long residentId);
}