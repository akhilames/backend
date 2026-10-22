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

    public AuthController(UserService userService, ResidentRepository residentRepository) {
        this.userService = userService;
        this.residentRepository = residentRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            User user = userService.login(username, password);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("name", user.getName() != null ? user.getName() : user.getUsername());
            response.put("role", user.getRole() != null ? user.getRole().getName() : "ADMIN");
            response.put("status", user.getStatus());

            if (user.getRole() != null && "RESIDENT".equals(user.getRole().getName())) {
                Resident resident = residentRepository.findByUsername(user.getUsername()).orElse(null);
                if (resident != null) {
                    response.put("residentId", resident.getId());
                }
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            // If error occurs, send exact error message to frontend
            return ResponseEntity.status(500).body(Map.of("message", "Backend Error: " + e.getMessage()));
        }
    }
}