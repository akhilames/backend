package com.waterbilling.backend;

import com.waterbilling.backend.entity.Role;
import com.waterbilling.backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WaterBillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaterBillingApplication.class, args);
    }

    // App start ayyinappudu roles automatic ga create avvadaniki
    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("COMMERCIAL_ADMIN").isEmpty()) {
                Role admin = new Role();
                admin.setName("COMMERCIAL_ADMIN");
                roleRepository.save(admin);
                System.out.println("✅ Created COMMERCIAL_ADMIN role in DB");
            }
            if (roleRepository.findByName("RESIDENT").isEmpty()) {
                Role resident = new Role();
                resident.setName("RESIDENT");
                roleRepository.save(resident);
                System.out.println("✅ Created RESIDENT role in DB");
            }
        };
    }
}