package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.availability.AvailabilityCreateRequestDTO;
import com.example.hospitalAppointmentSystem.dto.availability.AvailabilityResponseDTO;
import com.example.hospitalAppointmentSystem.model.DoctorAvailability;
import com.example.hospitalAppointmentSystem.model.DoctorProfile;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.service.AvailabilityService;
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

@RestController
@RequestMapping("/api/availabilities")
@PreAuthorize("hasRole('DOCTOR')")
@Tag(
        name = "Doctor Availability",
        description = "Doctors manage and expose their available appointment slots"
)
@SecurityRequirement(name = "bearerAuth")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final DoctorProfileService doctorService;
    private final UserService userService;

    public AvailabilityController(
            AvailabilityService availabilityService,
            DoctorProfileService doctorService,
            UserService userService
    ) {
        this.availabilityService = availabilityService;
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @Operation(
            summary = "Create availability slot",
            description = """
                    Allows a DOCTOR to create a new availability time slot.
                    
                    - Slot must have valid start and end times
                    - Slot is initially marked as AVAILABLE
                    - Only the authenticated doctor can create slots
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Availability slot created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AvailabilityResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date range or overlapping slot",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only DOCTOR users can create availability",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<AvailabilityResponseDTO> createAvailability(
            Authentication authentication,
            @Valid
            @RequestBody
            @Schema(description = "Availability creation request")
            AvailabilityCreateRequestDTO request
    ) {
        User user = userService.getByEmail(authentication.getName());
        DoctorProfile doctor = doctorService.getByUserId(user.getId());

        DoctorAvailability availability = availabilityService.createAvailability(
                doctor.getId(),
                request.getStartTime(),
                request.getEndTime()
        );

        return ResponseEntity.ok(toResponseDTO(availability));
    }

    @Operation(
            summary = "Get doctor availability",
            description = """
                    Returns all AVAILABLE time slots for a given doctor.
                    
                    - Includes only slots that can still be booked
                    - Accessible to all authenticated users
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Availability slots retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AvailabilityResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found",
                    content = @Content
            )
    })
    @GetMapping("/doctor/{doctorId}")
    public List<AvailabilityResponseDTO> getDoctorAvailability(@PathVariable Long doctorId) {
        return availabilityService
                .getAvailableSlots(doctorId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AvailabilityResponseDTO toResponseDTO(DoctorAvailability availability) {
        return new AvailabilityResponseDTO(
                availability.getId(),
                availability.getStartTime(),
                availability.getEndTime(),
                availability.getStatus()
        );
    }
}
