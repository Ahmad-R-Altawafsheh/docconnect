package com.ltuc.docconnect.service.impl.query;

import com.ltuc.docconnect.dto.response.AppointmentResponse;
import com.ltuc.docconnect.entity.Doctor;
import com.ltuc.docconnect.exception.BadRequestException;
import com.ltuc.docconnect.exception.ResourceNotFoundException;
import com.ltuc.docconnect.exception.UnauthorizedException;
import com.ltuc.docconnect.mapper.AppointmentMapper;
import com.ltuc.docconnect.repository.AppointmentRepository;
import com.ltuc.docconnect.repository.DoctorRepository;
import com.ltuc.docconnect.service.interfaces.query.DoctorQueryService;
import com.ltuc.docconnect.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorQueryServiceImpl implements DoctorQueryService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public List<AppointmentResponse> getDoctorAppointments(LocalDate date, Authentication authentication, Long doctorId) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Doctor doctor;
        if (isAdmin) {
            if (doctorId == null) {
                throw new BadRequestException(Messages.DOCTOR_ID_REQUIRED_FOR_ADMIN);
            }
            doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));
        } else {
            String username = authentication.getName();
            doctor = doctorRepository.findByUserUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "Username", username));

            if (doctorId != null && !doctor.getId().equals(doctorId)) {
                throw new UnauthorizedException(Messages.NOT_AUTHORIZED_APPOINTMENT_OWNERSHIP);
            }
        }

        return appointmentRepository.findByDoctorIdAndDate(doctor.getId(), date).stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }
}