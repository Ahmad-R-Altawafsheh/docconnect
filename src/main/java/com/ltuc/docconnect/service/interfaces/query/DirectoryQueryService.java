package com.ltuc.docconnect.service.interfaces.query;

import com.ltuc.docconnect.dto.response.DoctorDetailResponse;
import com.ltuc.docconnect.dto.response.SlotResponse;
import com.ltuc.docconnect.dto.response.SpecialtyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface DirectoryQueryService {

    List<SpecialtyResponse> getSpecialties();

    Page<DoctorDetailResponse> findDoctorsBySpecialty(String specialty, Pageable pageable);

    List<SlotResponse> getAvailableSlots(Long doctorId, LocalDate date);
}