package com.example.hospitalAppointmentSystem.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileRequest {

    @Size(max = 100)
    private String fullName;

    @Size(max = 20)
    private String phone;
}
