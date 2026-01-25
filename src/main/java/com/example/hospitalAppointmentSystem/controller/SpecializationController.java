package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.specialization.SpecializationCreateRequestDTO;
import com.example.hospitalAppointmentSystem.dto.specialization.SpecializationResponseDTO;
import com.example.hospitalAppointmentSystem.dto.specialization.SpecializationUpdateRequestDTO;
import com.example.hospitalAppointmentSystem.service.SpecializationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specializations")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @PostMapping
    public ResponseEntity<SpecializationResponseDTO> create(
            @RequestBody SpecializationCreateRequestDTO dto
    ) {
        return ResponseEntity.ok(
                specializationService.createSpecialization(dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<SpecializationResponseDTO>> getAll() {
        return ResponseEntity.ok(
                specializationService.getAllSpecializations()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecializationResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                specializationService.getSpecializationById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecializationResponseDTO> update(
            @PathVariable Long id,
            @RequestBody SpecializationUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(
                specializationService.updateSpecialization(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        specializationService.deleteSpecialization(id);
        return ResponseEntity.noContent().build();
    }
}

