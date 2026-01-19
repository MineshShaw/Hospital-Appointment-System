package com.example.hospitalAppointmentSystem.service;

import com.example.hospitalAppointmentSystem.dto.*;
import com.example.hospitalAppointmentSystem.model.Role;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final UserRepository userRepository;

    public List<DoctorResponse> getAllDoctors() {

        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.DOCTOR)
                .map(d -> new DoctorResponse(
                        d.getId(),
                        d.getEmail(),
                        d.getFullName()
                ))
                .toList();
    }

    public DoctorResponse getDoctorById(Long id) {

        User doctor = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.DOCTOR)
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found"));

        return new DoctorResponse(
                doctor.getId(),
                doctor.getEmail(),
                doctor.getFullName()
        );
    }
}
