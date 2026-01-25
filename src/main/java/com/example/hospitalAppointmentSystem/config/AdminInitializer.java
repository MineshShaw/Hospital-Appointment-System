package com.example.hospitalAppointmentSystem.config;

import com.example.hospitalAppointmentSystem.model.Role;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByEmail("admin@hospital.com").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@hospital.com");
                admin.setPasswordHash(encoder.encode("Admin123!"));
                admin.setEnabled(true);
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("ADMIN user created");
            }
        };
    }
}
