package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.appointment.AppointmentCreateRequestDTO;
import com.example.hospitalAppointmentSystem.dto.appointment.AppointmentResponseDTO;
import com.example.hospitalAppointmentSystem.model.*;
import com.example.hospitalAppointmentSystem.service.AppointmentService;
import com.example.hospitalAppointmentSystem.service.DoctorProfileService;
import com.example.hospitalAppointmentSystem.service.PatientProfileService;
import com.example.hospitalAppointmentSystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final PatientProfileService patientProfileService;
    private final DoctorProfileService doctorProfileService;

    public AppointmentController(
            AppointmentService appointmentService,
            UserService userService,
            PatientProfileService patientProfileService,
            DoctorProfileService doctorProfileService
    ) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.patientProfileService = patientProfileService;
        this.doctorProfileService = doctorProfileService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentResponseDTO book(
            Authentication authentication,
            @Valid @RequestBody AppointmentCreateRequestDTO dto
    ) {
        User user = userService.getByEmail(authentication.getName());

        Appointment appointment = appointmentService.bookAppointment(
                dto.getAvailabilityId(),
                user.getId(),
                dto.getReason(),
                dto.getSymptoms(),
                dto.getNotes()
        );

        return toResponseDTO(appointment);
    }


    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient/me")
    public List<AppointmentResponseDTO> getPatientAppointments(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());

        return appointmentService.getPatientAppointments(patientProfileService.getByUserId(user.getId()).getId())
                .stream().map(this::toResponseDTO).toList();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/me")
    public List<AppointmentResponseDTO> getDoctorAppointments(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());

        return appointmentService.getDoctorAppointments(doctorProfileService.getByUserId(user.getId()).getId())
                .stream().map(this::toResponseDTO).toList();
    }


    public AppointmentResponseDTO toResponseDTO(Appointment a) {

        DoctorProfile d = a.getAvailability().getDoctor();
        PatientProfile p = a.getPatient();

        return new AppointmentResponseDTO(
                a.getId(),
                a.getAvailability().getStartTime(),
                a.getStatus(),
                d.getFullName(),
                p.getFullName()
        );
    }

}

