package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Resident;
import com.waterbilling.backend.service.ResidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/residents")
public class ResidentController {

    private final ResidentService residentService;

    public ResidentController(
            ResidentService residentService) {

        this.residentService = residentService;
    }

    @PostMapping
    public ResponseEntity<Resident> createResident(
            @RequestBody Map<String, String> request) {

        Resident resident =
                residentService.createResident(
                        request.get("username"),
                        request.get("password"),
                        request.get("name"),
                        request.get("email"),
                        request.get("phone"),
                        request.get("address"),
                        Long.parseLong(
                                request.get("commercialAdminId")
                        )
                );

        return ResponseEntity.ok(resident);
    }

    @GetMapping
    public ResponseEntity<List<Resident>> getAllResidents() {

        return ResponseEntity.ok(
                residentService.getAllResidents()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resident> getResidentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                residentService.getResidentById(id)
        );
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Resident> getResidentByUsername(
            @PathVariable String username) {

        return ResponseEntity.ok(
                residentService.getResidentByUsername(username)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resident> updateResident(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Resident resident =
                residentService.updateResident(
                        id,
                        request.get("name"),
                        request.get("email"),
                        request.get("phone"),
                        request.get("address")
                );

        return ResponseEntity.ok(resident);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResident(
            @PathVariable Long id) {

        residentService.deleteResident(id);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Resident deleted successfully"
                )
        );
    }
}