package com.waterbilling.backend.repository;

import com.waterbilling.backend.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResidentRepository extends JpaRepository<Resident, Long> {

    Optional<Resident> findByEmail(String email);

    Optional<Resident> findByUsername(String username);
}
