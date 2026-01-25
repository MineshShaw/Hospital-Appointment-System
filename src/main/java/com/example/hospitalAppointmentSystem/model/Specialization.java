package com.example.hospitalAppointmentSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "specializations",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@Getter
@Setter
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    public Specialization(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Specialization(){}
}



