package com.ltuc.docconnect.controllers;

import com.ltuc.docconnect.dto.request.AvailabilityRequest;
import com.ltuc.docconnect.dto.request.ReportCreationRequest;
import com.ltuc.docconnect.dto.response.ApiResponse;
import com.ltuc.docconnect.dto.response.AppointmentResponse;
import com.ltuc.docconnect.dto.response.AvailabilityResponse;
import com.ltuc.docconnect.service.interfaces.command.DoctorCommandService;
import com.ltuc.docconnect.service.interfaces.query.DoctorQueryService;
import com.ltuc.docconnect.util.Messages;
import com.ltuc.docconnect.util.SecurityConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.DOCTOR_OR_ADMIN)
public class DoctorController {

    private final DoctorQueryService doctorQueryService;
    private final DoctorCommandService doctorCommandService;

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getDoctorAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long doctorId,
            Authentication authentication
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<AppointmentResponse> data = doctorQueryService.getDoctorAppointments(targetDate, authentication, doctorId);
        return ResponseEntity.ok(ApiResponse.success(data, String.format(Messages.APPOINTMENT_LIST_RETRIEVED_FOR_DATE, targetDate)));
    }

    @PostMapping("/appointments/{appointmentId}/reports")
    public ResponseEntity<ApiResponse<AppointmentResponse>> createReport(
            @PathVariable Long appointmentId,
            @Valid @RequestBody ReportCreationRequest request,
            Authentication authentication
    ) {
        AppointmentResponse data = doctorCommandService.createReport(appointmentId, request, authentication);
        return ResponseEntity.ok(ApiResponse.success(data, Messages.REPORT_CREATED_SUCCESS));
    }

    @PostMapping("/availabilities")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> addAvailability(
            @Valid @RequestBody AvailabilityRequest request,
            @RequestParam(required = false) Long doctorId,
            Authentication authentication
    ) {
        AvailabilityResponse data = doctorCommandService.addAvailability(request, authentication, doctorId);
        return new ResponseEntity<>(ApiResponse.success(data, Messages.AVAILABILITY_UPDATED_SUCCESS), HttpStatus.CREATED);
    }
}