package com.ltuc.docconnect.service.interfaces.query;

import com.ltuc.docconnect.dto.response.AppointmentResponse;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

public interface DoctorQueryService {

    List<AppointmentResponse> getDoctorAppointments(LocalDate date, Authentication authentication, Long doctorId);
}