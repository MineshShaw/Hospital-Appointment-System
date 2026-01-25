package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.patient.PatientProfileResponseDTO;
import com.example.hospitalAppointmentSystem.dto.patient.PatientProfileUpdateDTO;
import com.example.hospitalAppointmentSystem.model.PatientProfile;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.service.PatientProfileService;
import com.example.hospitalAppointmentSystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@PreAuthorize("hasRole('PATIENT')")
@Tag(
        name = "Patient Profile",
        description = "Patient profile management endpoints"
)
@SecurityRequirement(name = "bearerAuth")
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

    @Operation(
            summary = "Get my patient profile",
            description = """
                    Returns the authenticated patient's profile.
                    
                    - Uses JWT authentication
                    - Accessible only to PATIENT role
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient profile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PatientProfileResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden – PATIENT role required",
                    content = @Content
            )
    })
    @GetMapping("/me")
    public ResponseEntity<PatientProfileResponseDTO> getMyProfile(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(
                toResponseDTO(patientService.getByUserId(user.getId()))
        );
    }

    @Operation(
            summary = "Update my patient profile",
            description = """
                    Allows a patient to update their personal profile information.
                    
                    - Date of birth
                    - Gender
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient profile updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PatientProfileResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid update request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden – PATIENT role required",
                    content = @Content
            )
    })
    @PutMapping("/me")
    public ResponseEntity<PatientProfileResponseDTO> updateProfile(
            Authentication authentication,
            @Valid
            @RequestBody
            @Schema(description = "Patient profile update request")
            PatientProfileUpdateDTO request
    ) {
        User user = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(
                toResponseDTO(patientService.update(request, user.getId()))
        );
    }

    public PatientProfileResponseDTO toResponseDTO(PatientProfile patient) {
        return new PatientProfileResponseDTO(
                patient.getId(),
                patient.getFullName(),
                patient.getDateOfBirth(),
                patient.getGender()
        );
    }
}
