package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.doctor.DoctorProfileResponseDTO;
import com.example.hospitalAppointmentSystem.dto.doctor.DoctorProfileUpdateDTO;
import com.example.hospitalAppointmentSystem.model.DoctorProfile;
import com.example.hospitalAppointmentSystem.model.Specialization;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.service.DoctorProfileService;
import com.example.hospitalAppointmentSystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    private final DoctorProfileService doctorService;
    private final UserService userService;

    public DoctorController(
            DoctorProfileService doctorService,
            UserService userService
    ) {
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileResponseDTO> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(doctorService.getByUserId(id)));
    }

    @GetMapping("/me")
    public ResponseEntity<DoctorProfileResponseDTO> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getByEmail(email);

        return ResponseEntity.ok(toResponseDTO(doctorService.getByUserId(user.getId())));
    }

    @PutMapping("/me")
    public ResponseEntity<DoctorProfileResponseDTO> updateMyProfile(Authentication authentication, @RequestBody DoctorProfileUpdateDTO request) {
        String email = authentication.getName();
        User user = userService.getByEmail(email);
        return ResponseEntity.ok(toResponseDTO(doctorService.update(request, user.getId())));
    }

    @GetMapping("/search")
    public List<DoctorProfileResponseDTO> filterDoctors(@RequestParam(required = false) String name, @RequestParam(required = false) Long specializationId) {
        return doctorService.searchDoctors(name, specializationId).stream().map(this::toResponseDTO).toList();
    }

    public DoctorProfileResponseDTO toResponseDTO(DoctorProfile doctor) {
        return new DoctorProfileResponseDTO(doctor.getId(),
                doctor.getFullName(),
                doctor.getSpecializations().stream().map(Specialization::getName).collect(Collectors.toSet()),
                doctor.getYearsOfExperience(),
                doctor.getBio(),
                doctor.isActive());
    }
}



