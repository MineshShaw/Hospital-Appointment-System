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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication",
        description = "User registration and login endpoints"
)
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final PatientProfileService patientProfileService;
    private final DoctorProfileService doctorProfileService;

    public AuthController(
            AuthService authService,
            UserService userService,
            PatientProfileService patientProfileService,
            DoctorProfileService doctorProfileService
    ) {
        this.authService = authService;
        this.userService = userService;
        this.patientProfileService = patientProfileService;
        this.doctorProfileService = doctorProfileService;
    }

    @Operation(
            summary = "Register a new user",
            description = """
                    Registers a new user as either a PATIENT or DOCTOR.
                    
                    - Creates a User account
                    - Creates a PatientProfile or DoctorProfile based on userType
                    - Doctors are created in inactive state and must be approved by ADMIN
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content
            )
    })
    @PostMapping("/register")
    @Transactional
    public void register(
            @Valid
            @RequestBody
            @Schema(description = "Registration request payload")
            RegisterRequestDTO request
    ) {

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
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns a JWT token if credentials are valid"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid email or password",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid
            @RequestBody
            @Schema(description = "Login request payload")
            LoginRequestDTO request
    ) {
        String token = authService.authenticate(
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
