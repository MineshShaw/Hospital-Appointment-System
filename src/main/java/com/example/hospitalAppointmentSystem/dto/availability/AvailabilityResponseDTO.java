package com.example.hospitalAppointmentSystem.dto.availability;

import com.example.hospitalAppointmentSystem.model.AvailabilityStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AvailabilityResponseDTO {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AvailabilityStatus status;

    public AvailabilityResponseDTO(Long id, LocalDateTime endTime, LocalDateTime startTime, AvailabilityStatus status) {
        this.endTime = endTime;
        this.id = id;
        this.startTime = startTime;
        this.status = status;
    }
}
