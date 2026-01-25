package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.availability.AvailabilityCreateRequestDTO;
import com.example.hospitalAppointmentSystem.dto.availability.AvailabilityResponseDTO;
import com.example.hospitalAppointmentSystem.model.DoctorAvailability;
import com.example.hospitalAppointmentSystem.model.DoctorProfile;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.service.AvailabilityService;
import com.example.hospitalAppointmentSystem.service.DoctorProfileService;
import com.example.hospitalAppointmentSystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/availabilities")
@PreAuthorize("hasRole('DOCTOR')")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final DoctorProfileService doctorService;
    private final UserService userService;

    public AvailabilityController(
            AvailabilityService availabilityService,
            DoctorProfileService doctorService,
            UserService userService
    ) {
        this.availabilityService = availabilityService;
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<AvailabilityResponseDTO> createAvailability(
            Authentication authentication,
            @RequestBody AvailabilityCreateRequestDTO request
            ) {
        User user = userService.getByEmail(authentication.getName());
        DoctorProfile doctor = doctorService.getByUserId(user.getId());

        return ResponseEntity.ok(toResponseDTO(availabilityService.createAvailability(
                doctor.getId(),
                request.getStartTime(),
                request.getEndTime()
        )));
    }

    @GetMapping("/doctor/{doctorId}")
    public List<AvailabilityResponseDTO> getDoctorAvailability(@PathVariable Long doctorId) {
        return availabilityService.getAvailableSlots(doctorId).stream().map(this::toResponseDTO).toList();
    }

    public AvailabilityResponseDTO toResponseDTO(DoctorAvailability availability) {
        return new AvailabilityResponseDTO(
                availability.getId(),
                availability.getStartTime(),
                availability.getEndTime(),
                availability.getStatus()
        );
    }
}

