package com.example.hospitalAppointmentSystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.example.hospitalAppointmentSystem.model.AppointmentStatus;

@Getter
@Setter
public class UpdateAppointmentStatusRequest {

    @NotNull
    private AppointmentStatus status;
}

