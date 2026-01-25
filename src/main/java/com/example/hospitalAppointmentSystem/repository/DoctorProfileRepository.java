package com.example.hospitalAppointmentSystem.repository;

import com.example.hospitalAppointmentSystem.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

    Optional<DoctorProfile> findByUserId(Long userId);

    List<DoctorProfile> findByActiveTrue();

    boolean existsByUserId(Long userId);

    boolean existsBySpecializations_Id(Long specializationId);

    @Query("""
    SELECT DISTINCT d
    FROM DoctorProfile d
    LEFT JOIN d.specializations s
    WHERE d.active = true
      AND (:name IS NULL OR LOWER(d.fullName) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:specializationId IS NULL OR s.id = :specializationId)
""")
    List<DoctorProfile> searchActiveDoctors(
            @Param("name") String name,
            @Param("specializationId") Long specializationId
    );

}

