package com.example.hospitalAppointmentSystem.service;

import com.example.hospitalAppointmentSystem.model.*;
import com.example.hospitalAppointmentSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final DoctorAvailabilityRepository availabilityRepo;
    private final AppointmentRepository appointmentRepo;
    private final PatientProfileRepository patientRepo;

    public AppointmentService(
            DoctorAvailabilityRepository availabilityRepo,
            AppointmentRepository appointmentRepo,
            PatientProfileRepository patientRepo
    ) {
        this.availabilityRepo = availabilityRepo;
        this.appointmentRepo = appointmentRepo;
        this.patientRepo = patientRepo;
    }

    @Transactional
    public Appointment bookAppointment(
            Long availabilityId,
            Long patientUserId,
            String reason,
            String symptoms,
            String notes
    ) {
        DoctorAvailability availability =
                availabilityRepo.findByIdForUpdate(availabilityId)
                        .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        if (availability.getStatus() != AvailabilityStatus.AVAILABLE) {
            throw new IllegalStateException("Slot is not available");
        }

        PatientProfile patient =
                patientRepo.findByUserId(patientUserId)
                        .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        Appointment appointment = new Appointment();
        appointment.setAvailability(availability);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setReasonForVisit(reason);
        appointment.setSymptoms(symptoms);
        appointment.setPatientNotes(notes);
        appointment.setCreatedAt(LocalDateTime.now());

        availability.setStatus(AvailabilityStatus.BOOKED);

        appointmentRepo.save(appointment);
        availabilityRepo.save(availability);

        return appointment;
    }

    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);

        DoctorAvailability availability = appointment.getAvailability();
        availability.setStatus(AvailabilityStatus.AVAILABLE);

        appointmentRepo.save(appointment);
        availabilityRepo.save(availability);
    }

    public List<Appointment> getPatientAppointments(Long id) {
        return appointmentRepo.findByPatientId(id);
    }

    public List<Appointment> getDoctorAppointments(Long id) {
        return appointmentRepo.findByAvailabilityDoctorId(id);
    }
}
