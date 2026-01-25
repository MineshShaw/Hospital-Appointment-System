package com.example.hospitalAppointmentSystem.dto.specialization;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecializationCreateRequestDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String description;
}

