package com.example.hospitalAppointmentSystem.service;

import com.example.hospitalAppointmentSystem.dto.*;
import com.example.hospitalAppointmentSystem.model.DoctorAvailability;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.repository.DoctorAvailabilityRepository;
import com.example.hospitalAppointmentSystem.repository.UserRepository;
import com.example.hospitalAppointmentSystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public void createAvailability(CreateAvailabilityRequest request) {

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("Invalid time range");
        }

        User doctor = getCurrentDoctor();

        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());

        availabilityRepository.save(availability);
    }

    public List<AvailabilityResponse> getDoctorAvailability(Long doctorId) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found"));

        return availabilityRepository.findByDoctorAndBookedFalse(doctor)
                .stream()
                .map(a -> new AvailabilityResponse(
                        a.getId(),
                        a.getStartTime(),
                        a.getEndTime(),
                        a.isBooked()
                ))
                .toList();
    }

    private User getCurrentDoctor() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found"));
    }
}
