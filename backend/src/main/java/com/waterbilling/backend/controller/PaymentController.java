package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Payment;
import com.waterbilling.backend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> makePayment(
            @RequestBody Map<String, String> request) {

        Payment payment = paymentService.makePayment(
                Long.parseLong(request.get("billId")),
                Double.parseDouble(request.get("amount")),
                request.get("paymentMethod")
        );

        return ResponseEntity.ok(payment);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(id)
        );
    }

    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<Payment>> getPaymentsByResident(
            @PathVariable Long residentId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByResident(residentId)
        );
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<Payment>> getPaymentsByBill(
            @PathVariable Long billId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByBill(billId)
        );
    }
}