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
    private final EmailService emailService;

    public AppointmentService(
            DoctorAvailabilityRepository availabilityRepo,
            AppointmentRepository appointmentRepo,
            PatientProfileRepository patientRepo,
            EmailService emailService
    ) {
        this.availabilityRepo = availabilityRepo;
        this.appointmentRepo = appointmentRepo;
        this.patientRepo = patientRepo;
        this.emailService = emailService;
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

        String patientEmail = appointment.getPatient().getUser().getEmail();
        String doctorEmail = appointment.getAvailability().getDoctor().getUser().getEmail();

        String subject = "Appointment Confirmation";
        String text = "Dear " + appointment.getPatient().getFullName() + ",<br>" +
                "Your appointment with Dr. " + appointment.getAvailability().getDoctor().getFullName() +
                " on " + appointment.getAvailability().getStartTime() + " has been confirmed.";

        emailService.sendEmail(patientEmail, subject, text);
        emailService.sendEmail(doctorEmail, subject, "Dear Dr. " +
                appointment.getAvailability().getDoctor().getFullName() +
                ",<br>You have a new appointment with " + appointment.getPatient().getFullName() +
                " on " + appointment.getAvailability().getStartTime());


        return appointment;
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appointment.getPatient().getUser().getId().equals(userId)) throw new IllegalArgumentException("Cannot cancel other user's appointments");

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
