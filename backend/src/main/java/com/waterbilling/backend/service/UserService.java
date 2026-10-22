package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Role;
import com.waterbilling.backend.entity.User;
import com.waterbilling.backend.repository.RoleRepository;
import com.waterbilling.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Role getRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException(roleName + " role not found in DB"));
    }

    public User registerCommercialAdmin(String username, String password, String name, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setEmail(email);
        user.setRole(getRole("COMMERCIAL_ADMIN"));
        user.setStatus("APPROVED");
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setName(username);
            newUser.setEmail(username + "_" + System.currentTimeMillis() + "@demo.com");

            if (username.toLowerCase().contains("resident")) {
                newUser.setRole(getRole("RESIDENT"));
            } else {
                newUser.setRole(getRole("COMMERCIAL_ADMIN"));
            }
            newUser.setStatus("APPROVED");
            return userRepository.save(newUser);
        }
        return user;
    }

    public User approveCommercialAdmin(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setStatus("APPROVED");
        return userRepository.save(user);
    }

    public List<User> getPendingCommercialAdmins() {
        return userRepository.findAll().stream().filter(u -> "PENDING".equals(u.getStatus())).toList();
    }

    public User createResident(String username, String password, String name, String email) {
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