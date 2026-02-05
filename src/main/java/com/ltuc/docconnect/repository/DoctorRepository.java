package com.ltuc.docconnect.repository;

import com.ltuc.docconnect.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUserUsername(String username);

    @Query(value = "SELECT DISTINCT d FROM Doctor d " +
            "JOIN FETCH d.doctorSpecialties ds " +
            "JOIN FETCH ds.specialty s " +
            "WHERE LOWER(s.name) = LOWER(:specialtyName)",
            countQuery = "SELECT COUNT(DISTINCT d) FROM Doctor d " +
                    "JOIN d.doctorSpecialties ds " +
                    "JOIN ds.specialty s " +
                    "WHERE LOWER(s.name) = LOWER(:specialtyName)")
    Page<Doctor> findBySpecialtyName(@Param("specialtyName") String specialtyName, Pageable pageable);

    @Query("SELECT d FROM Doctor d " +
            "LEFT JOIN FETCH d.doctorSpecialties ds " +
            "LEFT JOIN FETCH ds.specialty " +
            "WHERE d.id = :id")
    Optional<Doctor> findById(@Param("id") Long id);
}