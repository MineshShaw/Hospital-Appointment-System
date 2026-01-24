package com.example.hospitalAppointmentSystem.service;

import com.example.hospitalAppointmentSystem.dto.*;
import com.example.hospitalAppointmentSystem.model.*;
import com.example.hospitalAppointmentSystem.repository.*;
import com.example.hospitalAppointmentSystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public void bookAppointment(BookAppointmentRequest request) {

        User patient = getCurrentUser();

        DoctorAvailability availability =
                availabilityRepository.findById(request.getAvailabilityId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Slot not found"));

        if (availability.isBooked()) {
            throw new IllegalStateException("Slot already booked");
        }

        availability.setBooked(true);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(availability.getDoctor());
        appointment.setAvailability(availability);
        appointment.setStatus(AppointmentStatus.REQUESTED);

        appointmentRepository.save(appointment);
        availabilityRepository.save(availability);
    }

    public List<AppointmentResponse> getMyAppointments() {

        User patient = getCurrentUser();

        return appointmentRepository.findByPatient(patient)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AppointmentResponse> getDoctorAppointments() {

        User doctor = getCurrentUser();

        return appointmentRepository.findByDoctor(doctor)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private boolean isValidTransition(
            AppointmentStatus current, AppointmentStatus next) {

        return switch (current) {
            case REQUESTED ->
                    next == AppointmentStatus.CONFIRMED ||
                            next == AppointmentStatus.CANCELLED;
            case CONFIRMED ->
                    next == AppointmentStatus.COMPLETED ||
                            next == AppointmentStatus.CANCELLED;
            default -> false;
        };
    }

    private Appointment getAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Appointment not found"));
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found"));
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getDoctor().getFullName(),
                a.getPatient().getFullName(),
                a.getAvailability().getStartTime(),
                a.getAvailability().getEndTime(),
                a.getStatus()
        );
    }

    private void validateOwnership(Appointment appointment) {

        User currentUser = getCurrentUser();

        boolean isPatientOwner =
                appointment.getPatient().getId().equals(currentUser.getId());

        boolean isDoctorOwner =
                appointment.getDoctor().getId().equals(currentUser.getId());

        if (!isPatientOwner && !isDoctorOwner) {
            throw new SecurityException("Access denied");
        }
    }

    public void cancelAppointment(Long id) {

        Appointment appointment = getAppointment(id);
        validateOwnership(appointment);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
                appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.getAvailability().setBooked(false);

        appointmentRepository.save(appointment);
    }

    public void updateAppointmentStatus(
            Long id, UpdateAppointmentStatusRequest request) {

        Appointment appointment = getAppointment(id);
        User currentUser = getCurrentUser();

        if (!appointment.getDoctor().getId().equals(currentUser.getId())) {
            throw new SecurityException("Only assigned doctor can update status");
        }

        AppointmentStatus newStatus = request.getStatus();

        if (!isValidTransition(appointment.getStatus(), newStatus)) {
            throw new IllegalStateException("Invalid status transition");
        }

        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
    }


}
