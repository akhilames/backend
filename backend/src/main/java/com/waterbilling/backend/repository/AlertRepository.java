package com.waterbilling.backend.repository;

import com.waterbilling.backend.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    // Resident ki unna alerts
    List<Alert> findByResidentId(Long residentId);

    // By status (UNREAD / READ)
    List<Alert> findByStatus(String status);

    // By type (OVERUSE / LEAK / BILL / PAYMENT)
    List<Alert> findByType(String type);

    // Resident + Status
    List<Alert> findByResidentIdAndStatus(Long residentId, String status);

    // Unread count for resident
    long countByResidentIdAndStatus(Long residentId, String status);
}
