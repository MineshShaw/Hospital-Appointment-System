package com.example.hospitalAppointmentSystem.dto.auth;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.usertype.UserType;

@Getter
@Setter
public class AuthResponseDTO {

    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }
}
