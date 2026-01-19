package com.example.hospitalAppointmentSystem.repository;

import com.example.hospitalAppointmentSystem.model.Appointment;
import com.example.hospitalAppointmentSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient(User patient);

    List<Appointment> findByDoctor(User doctor);

    Appointment save(Appointment appointment);
}

