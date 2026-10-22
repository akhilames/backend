package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Bill;
import com.waterbilling.backend.entity.Payment;
import com.waterbilling.backend.repository.BillRepository;
import com.waterbilling.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private BillRepository billRepository;

    public Payment makePayment(Long billId, Double amount, String method) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));

        // No strict amount matching. It takes whatever you input.
        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setAmount(amount);
        payment.setPaymentMethod(method);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus("SUCCESS");

        // Update bill status to PAID
        bill.setStatus("PAID");
        billRepository.save(bill);

        return paymentRepository.save(payment);
    }
}