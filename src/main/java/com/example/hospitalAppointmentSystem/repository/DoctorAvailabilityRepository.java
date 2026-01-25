package com.example.hospitalAppointmentSystem.repository;

import com.example.hospitalAppointmentSystem.model.AvailabilityStatus;
import com.example.hospitalAppointmentSystem.model.DoctorAvailability;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityRepository
        extends JpaRepository<DoctorAvailability, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select da
        from DoctorAvailability da
        where da.id = :id
    """)
    Optional<DoctorAvailability> findByIdForUpdate(@Param("id") Long id);

    List<DoctorAvailability> findByDoctorIdAndStatus(
            Long doctorId,
            AvailabilityStatus status
    );

    List<DoctorAvailability> findByDoctorIdAndStartTimeBetween(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );
}
