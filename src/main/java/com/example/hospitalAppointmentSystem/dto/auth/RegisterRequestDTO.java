package com.example.hospitalAppointmentSystem.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    @NotBlank
    private String password;

    @NotNull
    private UserType userType;
}
