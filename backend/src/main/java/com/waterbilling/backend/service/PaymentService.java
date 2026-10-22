package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.entity.Payment;
import com.waterbilling.backend.repository.BillRepository;
import com.waterbilling.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private BillRepository billRepository;

    // 1. Strict rules lekunda payment chese method
    public Payment makePayment(Long billId, Double amount, String method) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));

        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setAmount(amount);
        payment.setPaymentMethod(method);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus("SUCCESS");

        bill.setStatus("PAID");
        billRepository.save(bill);

        return paymentRepository.save(payment);
    }

    // 2. Controller ki kavalsina methods ivi
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> getPaymentsByResident(Long residentId) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getBill() != null &&
                        p.getBill().getProperty() != null &&
                        p.getBill().getProperty().getResident() != null &&
                        p.getBill().getProperty().getResident().getId().equals(residentId))
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByBill(Long billId) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getBill() != null && p.getBill().getId().equals(billId))
                .collect(Collectors.toList());
    }
}