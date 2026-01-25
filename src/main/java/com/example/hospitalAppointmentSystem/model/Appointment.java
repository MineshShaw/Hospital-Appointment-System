package com.example.hospitalAppointmentSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "appointments",
        uniqueConstraints = @UniqueConstraint(columnNames = "availability_id")
)
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_profile_id", nullable = false)
    private PatientProfile patient;

    @OneToOne(optional = false)
    @JoinColumn(name = "availability_id", nullable = false, unique = true)
    private DoctorAvailability availability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(nullable = false, length = 500)
    private String reasonForVisit;

    @Column(length = 1000)
    private String symptoms;

    @Column(length = 1000)
    private String patientNotes;

    private LocalDateTime createdAt;
}

