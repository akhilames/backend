package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.entity.Payment;
import com.waterbilling.backend.repository.BillRepository;
import com.waterbilling.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            BillRepository billRepository) {

        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
    }

    public Payment makePayment(
            Long billId,
            Double amount,
            String paymentMethod) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() ->
                        new RuntimeException("Bill not found"));

        if ("PAID".equalsIgnoreCase(bill.getStatus())) {
            throw new RuntimeException("Bill is already paid");
        }

        if (amount == null || amount <= 0) {
            throw new RuntimeException(
                    "Payment amount must be greater than zero");
        }

        if (!Double.valueOf(bill.getTotalAmount()).equals(amount)) {
            throw new RuntimeException(
                    "Payment amount must match bill amount");
        }

        Payment payment = new Payment();

        payment.setBill(bill);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);

        payment.setTransactionId(
                "TXN-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        payment.setStatus("SUCCESS");
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment =
                paymentRepository.save(payment);

        bill.setStatus("PAID");
        billRepository.save(bill);

        return savedPayment;
    }

    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {

        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"));
    }

    // Resident-specific payments
    public List<Payment> getPaymentsByResident(
            Long residentId) {

        return paymentRepository
                .findByBill_Property_Resident_Id(residentId);
    }

    public List<Payment> getPaymentsByBill(
            Long billId) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Bill not found"));

        return paymentRepository.findByBill(bill);
    }
}