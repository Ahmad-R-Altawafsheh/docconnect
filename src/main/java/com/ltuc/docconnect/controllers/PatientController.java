package com.ltuc.docconnect.controllers;

import com.ltuc.docconnect.dto.request.AppointmentBookingRequest;
import com.ltuc.docconnect.dto.response.ApiResponse;
import com.ltuc.docconnect.dto.response.AppointmentResponse;
import com.ltuc.docconnect.service.interfaces.command.PatientCommandService;
import com.ltuc.docconnect.service.interfaces.query.PatientQueryService;
import com.ltuc.docconnect.util.Messages;
import com.ltuc.docconnect.util.SecurityConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.PATIENT_OR_ADMIN)
public class PatientController {

    private final PatientCommandService patientCommandService;
    private final PatientQueryService patientQueryService;

    @PostMapping("/appointments")
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            @Valid @RequestBody AppointmentBookingRequest request,
            @RequestParam(required = false) Long patientId,
            Authentication authentication
    ) {
        AppointmentResponse data = patientCommandService.bookAppointment(request, authentication, patientId);
        String successMessage = String.format(Messages.APPOINTMENT_BOOKED_SUCCESS, data.getFakePaymentTransactionId());
        return new ResponseEntity<>(ApiResponse.success(data, successMessage), HttpStatus.CREATED);
    }

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getPatientHistory(
            @RequestParam(required = false) Long patientId,
            Authentication authentication
    ) {
        List<AppointmentResponse> data = patientQueryService.getPatientHistory(patientId, authentication);
        return ResponseEntity.ok(ApiResponse.success(data, Messages.PATIENT_HISTORY_RETRIEVED_SUCCESS));
    }

    @PatchMapping("/appointments/{appointmentId}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        AppointmentResponse data = patientCommandService.cancelAppointment(appointmentId, authentication);
        return ResponseEntity.ok(ApiResponse.success(data, Messages.APPOINTMENT_CANCELLED_SUCCESS));
    }
}