package com.example.hospitalAppointmentSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctor_profiles")
@Getter
@Setter
public class DoctorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private int yearsOfExperience;

    private String bio;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "doctor_specializations",
            joinColumns = @JoinColumn(name = "doctor_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "specialization_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"doctor_profile_id", "specialization_id"}
            )
    )
    private Set<Specialization> specializations = new HashSet<>();

}
