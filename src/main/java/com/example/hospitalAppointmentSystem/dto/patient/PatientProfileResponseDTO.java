package com.example.hospitalAppointmentSystem.dto.patient;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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


