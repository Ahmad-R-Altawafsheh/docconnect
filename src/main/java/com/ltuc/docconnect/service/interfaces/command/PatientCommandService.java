package com.ltuc.docconnect.service.interfaces.command;

import com.ltuc.docconnect.dto.request.AppointmentBookingRequest;
import com.ltuc.docconnect.dto.response.AppointmentResponse;
import org.springframework.security.core.Authentication;

public interface PatientCommandService {

    AppointmentResponse bookAppointment(AppointmentBookingRequest request, Authentication authentication, Long patientId);

    AppointmentResponse cancelAppointment(Long appointmentId, Authentication authentication);
}