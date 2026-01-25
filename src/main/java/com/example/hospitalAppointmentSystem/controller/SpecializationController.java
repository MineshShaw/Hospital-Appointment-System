package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.specialization.SpecializationCreateRequestDTO;
import com.example.hospitalAppointmentSystem.dto.specialization.SpecializationResponseDTO;
import com.example.hospitalAppointmentSystem.dto.specialization.SpecializationUpdateRequestDTO;
import com.example.hospitalAppointmentSystem.service.SpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specializations")
@Tag(
        name = "Specializations",
        description = "CRUD operations for medical specializations"
)
@SecurityRequirement(name = "bearerAuth")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @Operation(
            summary = "Create a new specialization",
            description = """
                    Creates a new medical specialization.
                    
                    Example: Cardiology, Neurology, Orthopedics
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Specialization created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpecializationResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid specialization data",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<SpecializationResponseDTO> create(
            @Valid
            @RequestBody
            @Schema(description = "Specialization creation request")
            SpecializationCreateRequestDTO dto
    ) {

        SpecializationResponseDTO responseDTO =
                specializationService.createSpecialization(dto);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Get all specializations",
            description = "Returns a list of all available medical specializations"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of specializations retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpecializationResponseDTO.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<SpecializationResponseDTO>> getAll() {
        return ResponseEntity.ok(
                specializationService.getAllSpecializations()
        );
    }

    @Operation(
            summary = "Get specialization by ID",
            description = "Fetch a specific specialization using its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Specialization found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpecializationResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Specialization not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SpecializationResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                specializationService.getSpecializationById(id)
        );
    }

    @Operation(
            summary = "Update a specialization",
            description = "Updates the name or details of an existing specialization"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Specialization updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpecializationResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Specialization not found",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<SpecializationResponseDTO> update(
            @PathVariable Long id,
            @Valid
            @RequestBody
            @Schema(description = "Specialization update request")
            SpecializationUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(
                specializationService.updateSpecialization(id, dto)
        );
    }

    @Operation(
            summary = "Delete a specialization",
            description = """
                    Permanently deletes a specialization.
                    
                    ⚠️ Should not be deleted if associated with doctors.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Specialization deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Specialization not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        specializationService.deleteSpecialization(id);
        return ResponseEntity.noContent().build();
    }
}
