package com.ltuc.docconnect.repository;

import com.ltuc.docconnect.entity.Appointment;
import com.ltuc.docconnect.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {


    boolean existsByPatientIdAndDateAndStartTimeAndStatus(
            Long patientId,
            LocalDate date,
            LocalTime startTime,
            AppointmentStatus status
    );

    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient p JOIN FETCH a.doctor d LEFT JOIN FETCH a.report r WHERE a.doctor.id = :doctorId AND a.date = :date")
    List<Appointment> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient p JOIN FETCH a.doctor d LEFT JOIN FETCH a.report r WHERE a.patient.id = :patientId ORDER BY a.date DESC")
    List<Appointment> findByPatientIdOrderByDateDesc(@Param("patientId") Long patientId);
}