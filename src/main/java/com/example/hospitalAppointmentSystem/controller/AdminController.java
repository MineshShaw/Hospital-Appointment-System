package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.doctor.DoctorProfileResponseDTO;
import com.example.hospitalAppointmentSystem.model.DoctorProfile;
import com.example.hospitalAppointmentSystem.model.Specialization;
import com.example.hospitalAppointmentSystem.service.DoctorProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/doctors")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DoctorProfileService doctorProfileService;

    public AdminController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<DoctorProfileResponseDTO> approveDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(doctorProfileService.approveDoctor(id)));
    }

    @DeleteMapping("/{id}/reject")
    public void rejectDoctor(@PathVariable Long id) {
        doctorProfileService.rejectDoctor(id);
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
