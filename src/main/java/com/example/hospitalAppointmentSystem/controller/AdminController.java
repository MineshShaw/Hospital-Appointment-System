package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.doctor.DoctorProfileResponseDTO;
import com.example.hospitalAppointmentSystem.model.DoctorProfile;
import com.example.hospitalAppointmentSystem.model.Specialization;
import com.example.hospitalAppointmentSystem.service.DoctorProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/doctors")
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Admin – Doctor Management",
        description = "Admin-only operations for approving or rejecting doctor profiles"
)
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final DoctorProfileService doctorProfileService;

    public AdminController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @Operation(
            summary = "Approve a doctor",
            description = "Approves a pending doctor profile. The doctor must have at least one specialization before approval."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor approved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DoctorProfileResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Doctor already approved or missing specialization",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor profile not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied (ADMIN only)",
                    content = @Content
            )
    })
    @PatchMapping("/{id}/approve")
    public ResponseEntity<DoctorProfileResponseDTO> approveDoctor(
            @PathVariable
            @Schema(description = "Doctor profile ID", example = "1")
            Long id
    ) {
        return ResponseEntity.ok(
                toResponseDTO(doctorProfileService.approveDoctor(id))
        );
    }

    @Operation(
            summary = "Reject a doctor",
            description = "Rejects and deletes a pending doctor profile. Approved doctors cannot be rejected."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Doctor rejected successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Approved doctors cannot be rejected",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor profile not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied (ADMIN only)",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}/reject")
    public void rejectDoctor(
            @PathVariable
            @Schema(description = "Doctor profile ID", example = "2")
            Long id
    ) {
        doctorProfileService.rejectDoctor(id);
    }

    private DoctorProfileResponseDTO toResponseDTO(DoctorProfile doctor) {
        return new DoctorProfileResponseDTO(
                doctor.getId(),
                doctor.getFullName(),
                doctor.getSpecializations()
                        .stream()
                        .map(Specialization::getName)
                        .collect(Collectors.toSet()),
                doctor.getYearsOfExperience(),
                doctor.getBio(),
                doctor.isActive()
        );
    }
}
