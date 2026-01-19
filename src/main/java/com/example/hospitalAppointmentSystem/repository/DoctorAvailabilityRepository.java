package com.example.hospitalAppointmentSystem.repository;

import com.example.hospitalAppointmentSystem.model.DoctorAvailability;
import com.example.hospitalAppointmentSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorAvailabilityRepository
        extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorAndBookedFalse(User doctor);

    List<DoctorAvailability> findByDoctor(User doctor);
}