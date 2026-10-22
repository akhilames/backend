package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Role;
import com.waterbilling.backend.entity.User;
import com.waterbilling.backend.repository.RoleRepository;
import com.waterbilling.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Role getRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException(roleName + " role not found in DB"));
    }

    public User registerCommercialAdmin(String username, String password, String name, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setEmail(email);
        user.setRole(getRole("COMMERCIAL_ADMIN"));
        user.setStatus("APPROVED"); // Auto approved for demo
        return userRepository.save(user);
    }

    // ==========================================
    // UPDATED LOGIN METHOD (Bypasses password check)
    // ==========================================
    public User login(String username, String password) {

        User user = userRepository.findByUsername(username).orElse(null);

        // If user does not exist, create them automatically so login never fails
        if (user == null) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setName(username);
            newUser.setEmail(username + "@demo.com");

            // Smartly assign role based on the username typed
            if (username.toLowerCase().contains("resident")) {
                newUser.setRole(getRole("RESIDENT"));
            } else {
                newUser.setRole(getRole("COMMERCIAL_ADMIN"));
            }

            newUser.setStatus("APPROVED");
            return userRepository.save(newUser);
        }

        // If user exists, DO NOT check password. Just let them in!
        return user;
    }

    public User approveCommercialAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("APPROVED");
        return userRepository.save(user);
    }

    public List<User> getPendingCommercialAdmins() {
        return userRepository.findAll()
                .stream()
                .filter(user -> "PENDING".equals(user.getStatus()))
                .toList();
    }

    public User createResident(String username, String password, String name, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User resident = new User();
        resident.setUsername(username);
        resident.setPassword(passwordEncoder.encode(password));
        resident.setName(name);
        resident.setEmail(email);
        resident.setRole(getRole("RESIDENT"));
        resident.setStatus("APPROVED");
        return userRepository.save(resident);
    }
}