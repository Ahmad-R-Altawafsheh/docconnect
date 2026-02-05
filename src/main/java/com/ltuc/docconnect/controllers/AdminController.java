package com.ltuc.docconnect.controllers;

import com.ltuc.docconnect.dto.request.DoctorCreationRequest;
import com.ltuc.docconnect.dto.request.SpecialtyRequest;
import com.ltuc.docconnect.dto.response.ApiResponse;
import com.ltuc.docconnect.dto.response.DoctorCreationResponse;
import com.ltuc.docconnect.dto.response.SpecialtyResponse;
import com.ltuc.docconnect.service.interfaces.command.AdminCommandService;
import com.ltuc.docconnect.util.Messages;
import com.ltuc.docconnect.util.SecurityConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.ADMIN_ONLY)
public class AdminController {

    private final AdminCommandService adminCommandService;

    @PostMapping("/specialties")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> createSpecialty(
            @Valid @RequestBody SpecialtyRequest request
    ) {
        SpecialtyResponse data = adminCommandService.createSpecialty(request);
        return new ResponseEntity<>(ApiResponse.success(data, Messages.SPECIALTY_CREATED_SUCCESS), HttpStatus.CREATED);
    }

    @PostMapping("/doctors")
    public ResponseEntity<ApiResponse<DoctorCreationResponse>> createDoctor(
            @Valid @RequestBody DoctorCreationRequest request
    ) {
        DoctorCreationResponse data = adminCommandService.createDoctor(request);
        return new ResponseEntity<>(ApiResponse.success(data, Messages.DOCTOR_REGISTERED_SUCCESS), HttpStatus.CREATED);
    }


}