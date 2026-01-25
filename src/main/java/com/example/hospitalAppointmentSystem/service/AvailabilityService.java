package com.example.hospitalAppointmentSystem.service;

import com.example.hospitalAppointmentSystem.model.AvailabilityStatus;
import com.example.hospitalAppointmentSystem.model.DoctorAvailability;
import com.example.hospitalAppointmentSystem.model.DoctorProfile;
import com.example.hospitalAppointmentSystem.repository.DoctorAvailabilityRepository;
import com.example.hospitalAppointmentSystem.repository.DoctorProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepo;
    private final DoctorProfileRepository doctorRepo;

    public AvailabilityService(
            DoctorAvailabilityRepository availabilityRepo,
            DoctorProfileRepository doctorRepo
    ) {
        this.availabilityRepo = availabilityRepo;
        this.doctorRepo = doctorRepo;
    }

    public DoctorAvailability createAvailability(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        DoctorProfile doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setStartTime(start);
        availability.setEndTime(end);
        availability.setStatus(AvailabilityStatus.AVAILABLE);

        return availabilityRepo.save(availability);
    }

    public List<DoctorAvailability> getAvailableSlots(Long doctorId) {
        return availabilityRepo.findByDoctorIdAndStatus(
                doctorId,
                AvailabilityStatus.AVAILABLE
        );
    }
}

