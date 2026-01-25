package com.example.hospitalAppointmentSystem.dto.auth;

import lombok.Getter;
import org.hibernate.usertype.UserType;

@Getter
public class AuthResponseDTO {

    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }
}
