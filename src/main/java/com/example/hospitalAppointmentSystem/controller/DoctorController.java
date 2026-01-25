package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.doctor.DoctorProfileResponseDTO;
import com.example.hospitalAppointmentSystem.dto.doctor.DoctorProfileUpdateDTO;
import com.example.hospitalAppointmentSystem.model.DoctorProfile;
import com.example.hospitalAppointmentSystem.model.Specialization;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.service.DoctorProfileService;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@PreAuthorize("hasRole('DOCTOR')")
@Tag(
        name = "Doctor Profile",
        description = "Doctor profile management and doctor discovery"
)
@SecurityRequirement(name = "bearerAuth")
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

    @Operation(
            summary = "Get doctor profile by user ID",
            description = """
                    Fetches a doctor's profile using the associated USER ID.
                    
                    - Used internally or by admin/doctor flows
                    - Requires DOCTOR role
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor profile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DoctorProfileResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor profile not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileResponseDTO> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(doctorService.getByUserId(id)));
    }

    @Operation(
            summary = "Get my doctor profile",
            description = """
                    Returns the authenticated doctor's own profile.
                    
                    - Uses JWT authentication
                    - No ID required
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor profile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DoctorProfileResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    @GetMapping("/me")
    public ResponseEntity<DoctorProfileResponseDTO> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getByEmail(email);

        return ResponseEntity.ok(toResponseDTO(doctorService.getByUserId(user.getId())));
    }

    @Operation(
            summary = "Update my doctor profile",
            description = """
                    Allows a doctor to update their own profile details.
                    
                    - Years of experience
                    - Bio
                    - Specializations
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor profile updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DoctorProfileResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid update request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only DOCTOR users can update profiles",
                    content = @Content
            )
    })
    @PutMapping("/me")
    public ResponseEntity<DoctorProfileResponseDTO> updateMyProfile(
            Authentication authentication,
            @Valid
            @RequestBody
            @Schema(description = "Doctor profile update request")
            DoctorProfileUpdateDTO request
    ) {
        String email = authentication.getName();
        User user = userService.getByEmail(email);

        return ResponseEntity.ok(
                toResponseDTO(doctorService.update(request, user.getId()))
        );
    }

    @PreAuthorize("hasRole('PATIENT')")
    @Operation(
            summary = "Search doctors",
            description = """
                    Search doctors using optional filters.
                    
                    - Filter by doctor name
                    - Filter by specialization
                    - Both filters are optional
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctors retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DoctorProfileResponseDTO.class)
                    )
            )
    })
    @GetMapping("/search")
    public List<DoctorProfileResponseDTO> filterDoctors(
            @RequestParam(required = false)
            @Schema(description = "Doctor name (partial match)")
            String name,

            @RequestParam(required = false)
            @Schema(description = "Specialization ID")
            Long specializationId
    ) {
        return doctorService
                .searchDoctors(name, specializationId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public DoctorProfileResponseDTO toResponseDTO(DoctorProfile doctor) {
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
