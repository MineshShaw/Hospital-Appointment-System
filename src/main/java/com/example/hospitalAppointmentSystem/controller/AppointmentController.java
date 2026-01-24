package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.*;
import com.example.hospitalAppointmentSystem.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    public ResponseEntity<Void> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request) {

        appointmentService.bookAppointment(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/me")
    public ResponseEntity<List<AppointmentResponse>> myAppointments() {
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<AppointmentResponse>> doctorAppointments() {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments());
    }

    @PreAuthorize("hasRole('PATIENT','DOCTOR')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequest request) {

        appointmentService.updateAppointmentStatus(id, request);
        return ResponseEntity.ok().build();
    }
}
