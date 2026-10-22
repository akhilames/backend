package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.Resident;
import com.waterbilling.backend.entity.User;
import com.waterbilling.backend.service.UserService;
import com.waterbilling.backend.repository.ResidentRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final ResidentRepository residentRepository;

    public AuthController(
            UserService userService,
            ResidentRepository residentRepository) {

        this.userService = userService;
        this.residentRepository = residentRepository;
    }

    @PostMapping("/register/commercial-admin")
    public ResponseEntity<?> registerCommercialAdmin(
            @RequestBody Map<String, String> request) {

        User user = userService.registerCommercialAdmin(
                request.get("username"),
                request.get("password"),
                request.get("name"),
                request.get("email")
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Commercial Admin registered successfully. Waiting for approval.",
                        "username",
                        user.getUsername(),
                        "status",
                        user.getStatus()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request) {

        User user = userService.login(
                request.get("username"),
                request.get("password")
        );

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Login successful");
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("name", user.getName());
        response.put("role", user.getRole().getName());  // ← FIXED: .name() → .getName()
        response.put("status", user.getStatus());

        // ← FIXED: Role is entity, compare by name string
        if (user.getRole() != null
                && "RESIDENT".equals(user.getRole().getName())) {

            Resident resident = residentRepository
                    .findByUsername(user.getUsername())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Resident record not found"));

            response.put("residentId", resident.getId());
        }

        return ResponseEntity.ok(response);
    }
}