package com.ltuc.docconnect.repository;

import com.ltuc.docconnect.entity.DoctorSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorSpecialtyRepository extends JpaRepository<DoctorSpecialty, Long> {

    boolean existsByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);

    void deleteByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);

    @Query("SELECT ds FROM DoctorSpecialty ds JOIN FETCH ds.specialty WHERE ds.doctor.id = :doctorId")
    List<DoctorSpecialty> findByDoctorIdWithSpecialty(@Param("doctorId") Long doctorId);
}