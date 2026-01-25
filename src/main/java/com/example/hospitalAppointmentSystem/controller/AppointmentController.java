package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.appointment.AppointmentCreateRequestDTO;
import com.example.hospitalAppointmentSystem.dto.appointment.AppointmentResponseDTO;
import com.example.hospitalAppointmentSystem.model.*;
import com.example.hospitalAppointmentSystem.service.AppointmentService;
import com.example.hospitalAppointmentSystem.service.DoctorProfileService;
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

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(
        name = "Appointments",
        description = "Booking, cancelling, and viewing appointments"
)
@SecurityRequirement(name = "bearerAuth")
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

    @Operation(
            summary = "Book an appointment",
            description = """
                    Allows a PATIENT to book an appointment using an availability slot.
                    
                    - Slot must exist
                    - Slot must not be already booked
                    - Appointment is created with status BOOKED
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment booked successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or slot unavailable",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only PATIENT users can book appointments",
                    content = @Content
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentResponseDTO book(
            Authentication authentication,
            @Valid
            @RequestBody
            @Schema(description = "Appointment booking request")
            AppointmentCreateRequestDTO dto
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

    @Operation(
            summary = "Cancel an appointment",
            description = """
                    Allows a PATIENT to cancel their appointment.
                    
                    - Only appointments owned by the patient can be cancelled
                    - Status is updated to CANCELLED
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Appointment cancelled successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only PATIENT users can cancel appointments",
                    content = @Content
            )
    })
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> cancel(Authentication authentication, @PathVariable Long id) {
        User user = userService.getByEmail(authentication.getName());
        appointmentService.cancelAppointment(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get patient appointments",
            description = "Returns all appointments booked by the logged-in patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only PATIENT users can access this endpoint",
                    content = @Content
            )
    })
    @GetMapping("/patient/me")
    @PreAuthorize("hasRole('PATIENT')")
    public List<AppointmentResponseDTO> getPatientAppointments(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());

        return appointmentService
                .getPatientAppointments(
                        patientProfileService.getByUserId(user.getId()).getId()
                )
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Operation(
            summary = "Get doctor appointments",
            description = "Returns all scheduled appointments for the logged-in doctor"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only DOCTOR users can access this endpoint",
                    content = @Content
            )
    })
    @GetMapping("/doctor/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AppointmentResponseDTO> getDoctorAppointments(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());

        return appointmentService
                .getDoctorAppointments(
                        doctorProfileService.getByUserId(user.getId()).getId()
                )
                .stream()
                .map(this::toResponseDTO)
                .toList();
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
