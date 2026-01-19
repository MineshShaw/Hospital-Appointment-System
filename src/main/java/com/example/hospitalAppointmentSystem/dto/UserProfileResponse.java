package com.example.hospitalAppointmentSystem.dto;

import com.example.hospitalAppointmentSystem.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String email;
    private Role role;
    private String fullName;
    private String phone;
}

