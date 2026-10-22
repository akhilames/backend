package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Payment;
import com.waterbilling.backend.repository.PaymentRepository;
import com.waterbilling.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired private PaymentService paymentService;
    @Autowired private PaymentRepository paymentRepository;

    @GetMapping
    public List<Payment> getAllPayments() { return paymentService.getAllPayments(); }

    @PostMapping
    public ResponseEntity<?> makePayment(@RequestBody Map<String, String> request) {
        Payment payment = paymentService.makePayment(Long.parseLong(request.get("billId")), Double.parseDouble(request.get("amount")), request.get("paymentMethod"));
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Payment payment = paymentRepository.findById(id).orElseThrow();
        payment.setAmount(Double.parseDouble(request.get("amount")));
        payment.setPaymentMethod(request.get("paymentMethod"));
        paymentRepository.save(payment);
        return ResponseEntity.ok(Map.of("message", "Payment updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        paymentRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Payment deleted"));
    }
}