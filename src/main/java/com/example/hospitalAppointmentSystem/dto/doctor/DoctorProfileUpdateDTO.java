package com.example.hospitalAppointmentSystem.dto.doctor;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DoctorProfileUpdateDTO {

    private Set<Long> specializationIds;
    private int yearsOfExperience;
    private String bio;
}

