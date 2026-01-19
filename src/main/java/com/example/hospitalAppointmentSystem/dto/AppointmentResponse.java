package com.example.hospitalAppointmentSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.example.hospitalAppointmentSystem.model.AppointmentStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AppointmentResponse {

    private Long id;
    private String doctorName;
    private String patientName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
}

