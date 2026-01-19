package com.example.hospitalAppointmentSystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookAppointmentRequest {

    @NotNull
    private Long availabilityId;
}
