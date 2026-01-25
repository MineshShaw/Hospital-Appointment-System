package com.example.hospitalAppointmentSystem.service;

import com.example.hospitalAppointmentSystem.repository.DoctorProfileRepository;
import com.example.hospitalAppointmentSystem.repository.PatientProfileRepository;
import com.example.hospitalAppointmentSystem.repository.UserRepository;
import com.example.hospitalAppointmentSystem.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorRepo;
    private final PatientProfileRepository patientRepo;

    public CustomUserDetailsService(
            UserRepository userRepository,
            DoctorProfileRepository doctorRepo,
            PatientProfileRepository patientRepo
    ) {
        this.userRepository = userRepository;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        List<GrantedAuthority> authorities = new ArrayList<>();

        // Derived roles
        if (doctorRepo.existsByUserId(user.getId())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_DOCTOR"));
        } else if (patientRepo.existsByUserId(user.getId())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PATIENT"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
}

