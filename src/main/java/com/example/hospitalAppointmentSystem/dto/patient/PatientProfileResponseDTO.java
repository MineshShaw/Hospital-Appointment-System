package com.example.hospitalAppointmentSystem.dto.patient;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PatientProfileResponseDTO {

    private Long id;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;

    public PatientProfileResponseDTO(
            Long id,
            String fullName,
            LocalDate dateOfBirth,
            String gender
    ) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }
}


