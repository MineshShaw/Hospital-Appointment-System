package com.example.hospitalAppointmentSystem.dto.appointment;

import com.example.hospitalAppointmentSystem.model.AppointmentStatus;
import lombok.Getter;

import java.util.Set;
import java.time.LocalDateTime;

@Getter
public class AppointmentResponseDTO {

    private Long id;
    private LocalDateTime startTime;
    private AppointmentStatus status;

    private String doctorName;
    private String patientName;

    public AppointmentResponseDTO(
            Long id,
            LocalDateTime startTime,
            AppointmentStatus status,
            String doctorName,
            String patientName
    ) {
        this.id = id;
        this.startTime = startTime;
        this.status = status;
        this.doctorName = doctorName;
        this.patientName = patientName;
    }
}


