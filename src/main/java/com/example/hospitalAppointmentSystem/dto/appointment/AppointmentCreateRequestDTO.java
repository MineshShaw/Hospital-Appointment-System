package com.example.hospitalAppointmentSystem.dto.appointment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentCreateRequestDTO {

    @NotNull
    private Long availabilityId;

    @NotBlank
    private String reason;

    private String symptoms;
    private String notes;
}

