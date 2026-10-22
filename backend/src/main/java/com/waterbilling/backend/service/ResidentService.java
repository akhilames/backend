package com.waterbilling.backend.service;

import com.waterbilling.backend.entity.Resident;
import com.waterbilling.backend.entity.Role;
import com.waterbilling.backend.entity.User;
import com.waterbilling.backend.repository.ResidentRepository;
import com.waterbilling.backend.repository.RoleRepository;
import com.waterbilling.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResidentService {

    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public ResidentService(
            ResidentRepository residentRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.residentRepository = residentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Resident createResident(
            String username,
            String password,
            String name,
            String email,
            String phone,
            String address,
            Long commercialAdminId) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User commercialAdmin = userRepository.findById(commercialAdminId)
                .orElseThrow(() -> new RuntimeException("Commercial Admin not found"));

        // Role check using Role.name (String)
        if (commercialAdmin.getRole() == null
                || !"COMMERCIAL_ADMIN".equals(commercialAdmin.getRole().getName())
                || !"APPROVED".equals(commercialAdmin.getStatus())) {
            throw new RuntimeException("Commercial Admin is not approved");
        }

        String encodedPassword = passwordEncoder.encode(password);

        // Fetch RESIDENT role from DB
        Role residentRole = roleRepository.findByName("RESIDENT")
                .orElseThrow(() -> new RuntimeException("RESIDENT role not found in DB"));

        User residentUser = new User();
        residentUser.setUsername(username);
        residentUser.setPassword(encodedPassword);
        residentUser.setName(name);
        residentUser.setEmail(email);
        residentUser.setRole(residentRole);
        residentUser.setStatus("APPROVED");

        userRepository.save(residentUser);

        Resident resident = new Resident();
        resident.setCommercialAdmin(commercialAdmin);
        resident.setUsername(username);
        resident.setPassword(encodedPassword);
        resident.setName(name);
        resident.setEmail(email);
        resident.setPhone(phone);
        resident.setAddress(address);

        return residentRepository.save(resident);
    }

    public List<Resident> getAllResidents() {
        return residentRepository.findAll();
    }

    public Resident getResidentById(Long id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resident not found"));
    }

    public Resident getResidentByUsername(String username) {
        return residentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Resident not found"));
    }

    public Resident updateResident(
            Long id,
            String name,
            String email,
            String phone,
            String address) {

        Resident resident = getResidentById(id);
        resident.setName(name);
        resident.setEmail(email);
        resident.setPhone(phone);
        resident.setAddress(address);

        return residentRepository.save(resident);
    }

    public void deleteResident(Long id) {
        if (!residentRepository.existsById(id)) {
            throw new RuntimeException("Resident not found");
        }
        residentRepository.deleteById(id);
    }
}