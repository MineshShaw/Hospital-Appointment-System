package com.example.hospitalAppointmentSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User patient;

    @ManyToOne(optional = false)
    private User doctor;

    @ManyToOne(optional = false)
    private DoctorAvailability availability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.REQUESTED;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
