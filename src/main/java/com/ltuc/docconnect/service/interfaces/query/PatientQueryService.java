package com.ltuc.docconnect.service.interfaces.query;

import com.ltuc.docconnect.dto.response.AppointmentResponse;
import org.springframework.security.core.Authentication;
import java.util.List;

public interface PatientQueryService {

    List<AppointmentResponse> getPatientHistory(Long patientId, Authentication authentication);
}