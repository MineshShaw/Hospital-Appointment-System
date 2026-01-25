package com.example.hospitalAppointmentSystem.dto.patient;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientProfileUpdateDTO {

    private LocalDate dateOfBirth;
    private String gender;
}
