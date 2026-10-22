package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Alert;
import com.waterbilling.backend.repository.AlertRepository;
import com.waterbilling.backend.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AlertService alertService;

    // =========================
    // GET ALL ALERTS
    // =========================
    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    // =========================
    // GET ALERTS BY RESIDENT
    // =========================
    @GetMapping("/resident/{residentId}")
    public List<Alert> getByResident(@PathVariable Long residentId) {
        return alertRepository.findByResidentId(residentId);
    }

    // =========================
    // GET UNREAD ALERTS BY RESIDENT
    // =========================
    @GetMapping("/resident/{residentId}/unread")
    public List<Alert> getUnreadByResident(@PathVariable Long residentId) {
        return alertRepository.findByResidentIdAndStatus(residentId, "UNREAD");
    }

    // =========================
    // MARK AS READ
    // =========================
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setStatus("READ");
        alertRepository.save(alert);
        return ResponseEntity.ok(Map.of("message", "Alert marked as read"));
    }

    // =========================
    // TRIGGER OVERUSE CHECK (manual)
    // =========================
    @PostMapping("/check-overuse")
    public ResponseEntity<?> triggerOveruseCheck() {
        alertService.checkAllResidentsForOveruse();
        return ResponseEntity.ok(Map.of("message", "Overuse check triggered"));
    }

    // =========================
    // TRIGGER BILL NOTIFICATIONS (manual)
    // =========================
    @PostMapping("/send-bill-notifications")
    public ResponseEntity<?> sendBillNotifications() {
        alertService.sendBillNotifications();
        return ResponseEntity.ok(Map.of("message", "Bill notifications sent"));
    }
}