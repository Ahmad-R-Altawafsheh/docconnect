package com.ltuc.docconnect.service.impl.query;

import com.ltuc.docconnect.dto.response.DoctorDetailResponse;
import com.ltuc.docconnect.dto.response.SlotResponse;
import com.ltuc.docconnect.dto.response.SpecialtyResponse;
import com.ltuc.docconnect.exception.ResourceNotFoundException;
import com.ltuc.docconnect.mapper.SpecialtyMapper;
import com.ltuc.docconnect.repository.DoctorRepository;
import com.ltuc.docconnect.repository.SpecialtyRepository;
import com.ltuc.docconnect.service.interfaces.query.DirectoryQueryService;
import com.ltuc.docconnect.service.slot.SlotAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DirectoryQueryServiceImpl implements DirectoryQueryService {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final SpecialtyMapper specialtyMapper;
    private final SlotAvailabilityService slotAvailabilityService;

    @Override
    public List<SpecialtyResponse> getSpecialties() {
        return specialtyMapper.toResponseList(specialtyRepository.findAll());
    }

    @Override
    public Page<DoctorDetailResponse> findDoctorsBySpecialty(String specialty, Pageable pageable) {
        if (!specialtyRepository.existsByNameIgnoreCase(specialty)) {
            throw new ResourceNotFoundException("Specialty", "name", specialty);
        }

        return doctorRepository.findBySpecialtyName(specialty, pageable)
                .map(doctor -> {
                    DoctorDetailResponse response = new DoctorDetailResponse();
                    response.setId(doctor.getId());
                    response.setFullName(doctor.getFullName());
                    response.setBio(doctor.getBio());
                    response.setNextAvailableSlot(slotAvailabilityService.findNextAvailableSlot(doctor.getId()));
                    return response;
                });
    }

    @Override
    public List<SlotResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        return slotAvailabilityService.findAllFreeSlots(doctorId, date);
    }
}