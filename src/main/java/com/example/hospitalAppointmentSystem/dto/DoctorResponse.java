package com.example.hospitalAppointmentSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DoctorResponse {

    private Long id;
    private String email;
    private String fullName;
}