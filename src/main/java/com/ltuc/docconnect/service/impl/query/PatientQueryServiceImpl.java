package com.ltuc.docconnect.service.impl.query;

import com.ltuc.docconnect.dto.response.AppointmentResponse;
import com.ltuc.docconnect.entity.Patient;
import com.ltuc.docconnect.exception.BadRequestException;
import com.ltuc.docconnect.exception.ResourceNotFoundException;
import com.ltuc.docconnect.exception.UnauthorizedException;
import com.ltuc.docconnect.mapper.AppointmentMapper;
import com.ltuc.docconnect.repository.AppointmentRepository;
import com.ltuc.docconnect.repository.PatientRepository;
import com.ltuc.docconnect.service.interfaces.query.PatientQueryService;
import com.ltuc.docconnect.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PatientQueryServiceImpl implements PatientQueryService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public List<AppointmentResponse> getPatientHistory(Long patientId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Patient patient;

        if (isAdmin) {
            if (patientId == null) {
                throw new BadRequestException(Messages.PATIENT_ID_REQUIRED_FOR_ADMIN);
            }
            patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));
        } else {
            patient = patientRepository.findByUserUsername(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient", "Username", authentication.getName()));

            if (patientId != null && !patient.getId().equals(patientId)) {
                throw new UnauthorizedException(Messages.NOT_AUTHORIZED_APPOINTMENT_OWNERSHIP);
            }
        }

        return appointmentRepository.findByPatientIdOrderByDateDesc(patient.getId()).stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }
}