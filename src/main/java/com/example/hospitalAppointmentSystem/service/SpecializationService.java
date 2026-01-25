package com.example.hospitalAppointmentSystem.service;

import com.example.hospitalAppointmentSystem.dto.specialization.SpecializationCreateRequestDTO;
import com.example.hospitalAppointmentSystem.dto.specialization.SpecializationResponseDTO;
import com.example.hospitalAppointmentSystem.dto.specialization.SpecializationUpdateRequestDTO;
import com.example.hospitalAppointmentSystem.model.Specialization;
import com.example.hospitalAppointmentSystem.repository.DoctorProfileRepository;
import com.example.hospitalAppointmentSystem.repository.SpecializationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public SpecializationService(
            SpecializationRepository specializationRepository,
            DoctorProfileRepository doctorProfileRepository
    ) {
        this.specializationRepository = specializationRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    public SpecializationResponseDTO createSpecialization(
            SpecializationCreateRequestDTO dto
    ) {
        Specialization specialization = new Specialization();
        specialization.setName(dto.getName().trim());

        specializationRepository.save(specialization);

        return mapToResponseDTO(specialization);
    }

    @Transactional(readOnly = true)
    public List<SpecializationResponseDTO> getAllSpecializations() {
        return specializationRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SpecializationResponseDTO getSpecializationById(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Specialization not found with id: " + id)
                );

        return mapToResponseDTO(specialization);
    }

    public SpecializationResponseDTO updateSpecialization(
            Long id,
            SpecializationUpdateRequestDTO dto
    ) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Specialization not found with id: " + id)
                );

        specialization.setName(dto.getName().trim());

        return mapToResponseDTO(specialization);
    }

    public void deleteSpecialization(Long id) {

        if (!specializationRepository.existsById(id)) {
            throw new RuntimeException("Specialization not found with id: " + id);
        }

        boolean isUsed = doctorProfileRepository
                .existsBySpecializations_Id(id);

        if (isUsed) {
            throw new RuntimeException(
                    "Cannot delete specialization: it is assigned to one or more doctors"
            );
        }

        specializationRepository.deleteById(id);
    }

    private SpecializationResponseDTO mapToResponseDTO(
            Specialization specialization
    ) {
        SpecializationResponseDTO dto = new SpecializationResponseDTO();
        dto.setId(specialization.getId());
        dto.setName(specialization.getName());
        return dto;
    }
}



