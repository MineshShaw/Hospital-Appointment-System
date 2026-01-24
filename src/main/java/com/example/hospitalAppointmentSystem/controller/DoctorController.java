package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.*;
import com.example.hospitalAppointmentSystem.service.DoctorAvailabilityService;
import com.example.hospitalAppointmentSystem.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorAvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/availability")
    public ResponseEntity<Void> addAvailability(
            @Valid @RequestBody CreateAvailabilityRequest request) {
        System.out.println("request received");
        availabilityService.createAvailability(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<List<AvailabilityResponse>> getAvailability(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                availabilityService.getDoctorAvailability(id));
    }
}

