package com.example.hospitalAppointmentSystem.dto;

import jakarta.validation.constraints.*;
import com.example.hospitalAppointmentSystem.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password should have minimum 8 characters")
    private String password;

    @NotNull
    private Role role;
}
