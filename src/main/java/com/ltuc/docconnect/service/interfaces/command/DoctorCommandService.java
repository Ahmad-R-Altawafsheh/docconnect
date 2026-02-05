package com.ltuc.docconnect.service.interfaces.command;

import com.ltuc.docconnect.dto.request.AvailabilityRequest;
import com.ltuc.docconnect.dto.request.ReportCreationRequest;
import com.ltuc.docconnect.dto.response.AppointmentResponse;
import com.ltuc.docconnect.dto.response.AvailabilityResponse;
import org.springframework.security.core.Authentication;

public interface DoctorCommandService {

    AvailabilityResponse addAvailability(AvailabilityRequest request, Authentication authentication, Long doctorId);

    AppointmentResponse createReport(Long appointmentId, ReportCreationRequest request, Authentication authentication);
}