package com.example.hospitalAppointmentSystem.repository;

import com.example.hospitalAppointmentSystem.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByAvailabilityDoctorId(Long doctorId);
}

