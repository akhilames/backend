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

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

    // ==========================================
    // UPDATED LOGIN METHOD (Auto-Creates User)
    // ==========================================
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);

        // User DB lo lekapothe, automatic ga create chestundi
        if (user == null) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setName(username);
            newUser.setEmail(username + "@demo.com");

            // Username lo resident unte Resident role, lekapothe Admin role
            if (username.toLowerCase().contains("resident")) {
                newUser.setRole(getRole("RESIDENT"));
            } else {
                newUser.setRole(getRole("COMMERCIAL_ADMIN"));
            }

            newUser.setStatus("APPROVED");
            return userRepository.save(newUser);
        }

        // User unte password check cheyyadu, direct ga login cheyistundi (Demo kosam)
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