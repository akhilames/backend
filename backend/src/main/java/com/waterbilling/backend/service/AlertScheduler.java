package com.waterbilling.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AlertScheduler {

    @Autowired
    private AlertService alertService;

    // =========================
    // DAILY OVERUSE CHECK - Every day 8:00 AM
    // =========================
    @Scheduled(cron = "0 0 8 * * *")
    public void dailyOveruseCheck() {
        System.out.println("⏰ [Scheduler] Starting daily overuse check...");
        try {
            alertService.checkAllResidentsForOveruse();
            System.out.println("✅ [Scheduler] Overuse check completed");
        } catch (Exception e) {
            System.err.println("❌ [Scheduler] Error: " + e.getMessage());
        }
    }

    // =========================
    // HOURLY LEAK CHECK - Every hour
    // =========================
    @Scheduled(cron = "0 0 * * * *")
    public void hourlyLeakCheck() {
        System.out.println("⏰ [Scheduler] Starting hourly leak check...");
        try {
            alertService.checkAllResidentsForOveruse();
        } catch (Exception e) {
            System.err.println("❌ [Scheduler] Leak check error: " + e.getMessage());
        }
    }

    // =========================
    // MONTHLY BILL NOTIFICATIONS - 1st of every month 9:00 AM
    // =========================
    @Scheduled(cron = "0 0 9 1 * *")
    public void monthlyBillNotifications() {
        System.out.println("⏰ [Scheduler] Sending monthly bill notifications...");
        try {
            alertService.sendBillNotifications();
            System.out.println("✅ [Scheduler] Bill notifications sent");
        } catch (Exception e) {
            System.err.println("❌ [Scheduler] Bill notification error: " + e.getMessage());
        }
    }
}