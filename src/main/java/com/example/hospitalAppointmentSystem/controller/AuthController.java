package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.auth.AuthResponseDTO;
import com.example.hospitalAppointmentSystem.dto.auth.LoginRequestDTO;
import com.example.hospitalAppointmentSystem.dto.auth.RegisterRequestDTO;
import com.example.hospitalAppointmentSystem.dto.auth.UserType;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.service.AuthService;
import com.example.hospitalAppointmentSystem.service.DoctorProfileService;
import com.example.hospitalAppointmentSystem.service.PatientProfileService;
import com.example.hospitalAppointmentSystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final PatientProfileService patientProfileService;
    private final DoctorProfileService doctorProfileService;

    public AuthController(AuthService authService, UserService userService, PatientProfileService patientProfileService, DoctorProfileService doctorProfileService) {
        this.authService = authService;
        this.userService = userService;
        this.patientProfileService = patientProfileService;
        this.doctorProfileService = doctorProfileService;
    }

    @PostMapping("/register")
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {

        User user = userService.registerUser(
                request.getEmail(),
                request.getPassword()
        );

        if (request.getUserType() == UserType.PATIENT) {
            patientProfileService.create(user, request.getFullName());
        }

        if (request.getUserType() == UserType.DOCTOR) {
            doctorProfileService.create(user, request.getFullName());
        }

        String token = authService.authenticate(user.getEmail(), user.getPasswordHash());
        return new AuthResponseDTO(token);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody LoginRequestDTO request
            ) {
        String token = authService.authenticate(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}

