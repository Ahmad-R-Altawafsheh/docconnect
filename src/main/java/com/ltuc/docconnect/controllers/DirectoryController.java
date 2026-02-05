package com.ltuc.docconnect.controllers;

import com.ltuc.docconnect.dto.response.ApiResponse;
import com.ltuc.docconnect.dto.response.DoctorDetailResponse;
import com.ltuc.docconnect.dto.response.SlotResponse;
import com.ltuc.docconnect.dto.response.SpecialtyResponse;
import com.ltuc.docconnect.service.interfaces.query.DirectoryQueryService;
import com.ltuc.docconnect.util.Messages;
import com.ltuc.docconnect.util.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.ANY_USER)
public class DirectoryController {

    private final DirectoryQueryService directoryQueryService;

    @GetMapping("/specialties")
    public ResponseEntity<ApiResponse<List<SpecialtyResponse>>> getSpecialties() {
        List<SpecialtyResponse> data = directoryQueryService.getSpecialties();
        return ResponseEntity.ok(ApiResponse.success(data, Messages.SPECIALTIES_RESPONSE_SUCCESS));
    }

    @GetMapping("/doctors")
    public ResponseEntity<ApiResponse<Page<DoctorDetailResponse>>> findDoctors(
            @RequestParam String specialty,
            Pageable pageable
    ) {
        Page<DoctorDetailResponse> data = directoryQueryService.findDoctorsBySpecialty(specialty, pageable);
        return ResponseEntity.ok(ApiResponse.success(data, Messages.DOCTOR_LIST_RETRIEVED));
    }

    @GetMapping("/doctors/{doctorId}/slots")
    public ResponseEntity<ApiResponse<List<SlotResponse>>> getDoctorAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<SlotResponse> data = directoryQueryService.getAvailableSlots(doctorId, date);
        String message = String.format(Messages.AVAILABLE_SLOTS_RETRIEVED_FOR_DATE, date.toString());
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }
}