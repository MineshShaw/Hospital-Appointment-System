package com.example.hospitalAppointmentSystem.service;

import com.example.hospitalAppointmentSystem.dto.doctor.DoctorProfileUpdateDTO;
import com.example.hospitalAppointmentSystem.model.DoctorProfile;
import com.example.hospitalAppointmentSystem.model.Specialization;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.repository.DoctorProfileRepository;

import com.example.hospitalAppointmentSystem.repository.SpecializationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class DoctorProfileService {

    private final DoctorProfileRepository doctorRepo;
    private final SpecializationRepository specializationRepository;

    public DoctorProfileService(DoctorProfileRepository doctorRepo, SpecializationRepository specializationRepository) {
        this.doctorRepo = doctorRepo;
        this.specializationRepository = specializationRepository;
    }

    public DoctorProfile getByUserId(Long userId) {
        return doctorRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Doctor profile not found"));
    }

    public List<DoctorProfile> getAllDoctors() {
        return doctorRepo.findByActiveTrue();
    }

    public void create(User user, String fullName) {
        DoctorProfile doctorProfile = new DoctorProfile();
        doctorProfile.setFullName(fullName);
        doctorProfile.setUser(user);
        doctorProfile.setActive(false);
        doctorRepo.save(doctorProfile);
    }

    public DoctorProfile update(DoctorProfileUpdateDTO updateDTO, Long id) {
        Set<Long> specializations = updateDTO.getSpecializationIds().stream().collect(Collectors.toSet());
        DoctorProfile doctor = getByUserId(id);
        doctor.setSpecializations(specializationRepository.findAllById(specializations).stream().collect(Collectors.toSet()));
        doctor.setBio(updateDTO.getBio());
        doctor.setYearsOfExperience(updateDTO.getYearsOfExperience());
        doctorRepo.save(doctor);
        return doctor;
    }

    public DoctorProfile approveDoctor(Long doctorProfileId) {

        DoctorProfile doctor = doctorRepo.findById(doctorProfileId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor profile not found")
                );

        if (doctor.isActive()) {
            throw new RuntimeException("Doctor is already approved");
        }

        if (doctor.getSpecializations() == null || doctor.getSpecializations().isEmpty()) {
            throw new RuntimeException(
                    "Doctor must have at least one specialization before approval"
            );
        }

        doctor.setActive(true);

        return doctor;
    }

    public void rejectDoctor(Long doctorProfileId) {

        DoctorProfile doctor = doctorRepo.findById(doctorProfileId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor profile not found")
                );

        if (doctor.isActive()) {
            throw new RuntimeException("Approved doctors cannot be rejected");
        }

        doctorRepo.delete(doctor);

    }

    public List<DoctorProfile> searchDoctors(
            String name,
            Long specializationId
    ) {

        List<DoctorProfile> doctors =
                doctorRepo.searchActiveDoctors(
                        normalize(name),
                        specializationId
                );

        return doctors;
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}

