package com.example.hospitalAppointmentSystem.service;

import com.example.hospitalAppointmentSystem.dto.patient.PatientProfileUpdateDTO;
import com.example.hospitalAppointmentSystem.model.PatientProfile;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.repository.PatientProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientProfileService {

    private final PatientProfileRepository patientRepo;

    public PatientProfileService(PatientProfileRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    public PatientProfile getByUserId(Long userId) {
        return patientRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Patient profile not found"));
    }

    public void create(User user, String fullname) {
        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(user);
        patientProfile.setFullName(fullname);
        patientRepo.save(patientProfile);
    }

    public PatientProfile update(PatientProfileUpdateDTO request, Long id) {
        PatientProfile patient = getByUserId(id);
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patientRepo.save(patient);
        return patient;
    }
}
