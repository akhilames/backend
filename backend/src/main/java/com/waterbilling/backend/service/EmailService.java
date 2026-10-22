package com.waterbilling.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // =========================
    // GENERIC EMAIL
    // =========================
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("Email sent to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    // =========================
    // BILL NOTIFICATION
    // =========================
    public void sendBillNotification(String to, String residentName,
                                     String billingMonth, double amount,
                                     String dueDate) {
        String subject = "💧 Your Water Bill - " + billingMonth;
        String body = "Dear " + residentName + ",\n\n"
                + "Your water bill for " + billingMonth + " has been generated.\n\n"
                + "Total Amount: ₹" + amount + "\n"
                + "Due Date: " + dueDate + "\n\n"
                + "Please pay before the due date to avoid penalties.\n\n"
                + "Thank you,\n"
                + "Water Billing Management System";
        sendEmail(to, subject, body);
    }

    // =========================
    // OVERUSE ALERT
    // =========================
    public void sendOveruseAlert(String to, String residentName,
                                 double consumption, double threshold) {
        String subject = "⚠️ Water Overuse Alert";
        String body = "Dear " + residentName + ",\n\n"
                + "Your water consumption has exceeded the threshold.\n\n"
                + "Today's Usage: " + consumption + " L\n"
                + "Threshold: " + threshold + " L\n\n"
                + "Please reduce your water usage to conserve water.\n\n"
                + "Thank you,\n"
                + "Water Billing Management System";
        sendEmail(to, subject, body);
    }

    // =========================
    // LEAK / ANOMALY ALERT
    // =========================
    public void sendLeakAlert(String to, String residentName,
                              double todayUsage, double averageUsage) {
        String subject = "🚨 Possible Water Leak Detected";
        String body = "Dear " + residentName + ",\n\n"
                + "We have detected abnormal water usage at your property.\n\n"
                + "Today's Usage: " + todayUsage + " L\n"
                + "Your Average: " + averageUsage + " L\n\n"
                + "This may indicate a water leak. Please check your pipes and fixtures.\n\n"
                + "Thank you,\n"
                + "Water Billing Management System";
        sendEmail(to, subject, body);
    }

    // =========================
    // PAYMENT CONFIRMATION
    // =========================
    public void sendPaymentConfirmation(String to, String residentName,
                                        double amount, String transactionId) {
        String subject = "✅ Payment Successful - Water Bill";
        String body = "Dear " + residentName + ",\n\n"
                + "Your payment has been received successfully.\n\n"
                + "Amount: ₹" + amount + "\n"
                + "Transaction ID: " + transactionId + "\n\n"
                + "Thank you for your payment.\n\n"
                + "Water Billing Management System";
        sendEmail(to, subject, body);
    }
}
