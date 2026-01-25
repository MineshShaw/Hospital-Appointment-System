package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.patient.PatientProfileResponseDTO;
import com.example.hospitalAppointmentSystem.dto.patient.PatientProfileUpdateDTO;
import com.example.hospitalAppointmentSystem.model.PatientProfile;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.service.PatientProfileService;
import com.example.hospitalAppointmentSystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    private final PatientProfileService patientService;
    private final UserService userService;

    public PatientController(
            PatientProfileService patientService,
            UserService userService
    ) {
        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<PatientProfileResponseDTO> getMyProfile(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(toResponseDTO(patientService.getByUserId(user.getId())));
    }

    @PutMapping("/me")
    public ResponseEntity<PatientProfileResponseDTO> updateProfile(Authentication authentication, @RequestBody PatientProfileUpdateDTO request) {
        User user = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(toResponseDTO(patientService.update(request, user.getId())));
    }

    public PatientProfileResponseDTO toResponseDTO(PatientProfile patient) {
        return new PatientProfileResponseDTO(patient.getId(), patient.getFullName(), patient.getDateOfBirth(), patient.getGender());
    }
}

