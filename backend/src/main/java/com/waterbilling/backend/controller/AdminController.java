package com.waterbilling.backend.controller;

import com.waterbilling.backend.entity.User;
import com.waterbilling.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/commercial-admins/pending")
    public ResponseEntity<List<User>> getPendingCommercialAdmins() {
        return ResponseEntity.ok(
                userService.getPendingCommercialAdmins()
        );
    }

    @PutMapping("/commercial-admins/{id}/approve")
    public ResponseEntity<?> approveCommercialAdmin(
            @PathVariable Long id) {

        User user = userService.approveCommercialAdmin(id);

        return ResponseEntity.ok(Map.of(
                "message", "Commercial Admin approved successfully",
                "username", user.getUsername(),
                "status", user.getStatus()
        ));
    }
}
