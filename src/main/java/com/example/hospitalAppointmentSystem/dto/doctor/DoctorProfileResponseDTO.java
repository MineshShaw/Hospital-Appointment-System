package com.example.hospitalAppointmentSystem.dto.doctor;

import lombok.Getter;

import java.util.Set;

@Getter
public class DoctorProfileResponseDTO {

    private Long id;
    private String fullName;
    private Set<String> specializations;
    private int yearsOfExperience;
    private String bio;
    private boolean active;

    public DoctorProfileResponseDTO(
            Long id,
            String fullName,
            Set<String> specializations,
            int yearsOfExperience,
            String bio,
            boolean active
    ) {
        this.id = id;
        this.fullName = fullName;
        this.specializations = specializations;
        this.yearsOfExperience = yearsOfExperience;
        this.bio = bio;
        this.active = active;
    }
}


