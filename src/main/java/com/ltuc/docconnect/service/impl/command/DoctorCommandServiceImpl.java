package com.ltuc.docconnect.service.impl.command;

import com.ltuc.docconnect.dto.request.AvailabilityRequest;
import com.ltuc.docconnect.dto.request.ReportCreationRequest;
import com.ltuc.docconnect.dto.response.AppointmentResponse;
import com.ltuc.docconnect.dto.response.AvailabilityResponse;
import com.ltuc.docconnect.entity.Appointment;
import com.ltuc.docconnect.entity.Availability;
import com.ltuc.docconnect.entity.Doctor;
import com.ltuc.docconnect.entity.Report;
import com.ltuc.docconnect.enums.AppointmentStatus;
import com.ltuc.docconnect.exception.BadRequestException;
import com.ltuc.docconnect.exception.BusinessLogicException;
import com.ltuc.docconnect.exception.ResourceNotFoundException;
import com.ltuc.docconnect.exception.UnauthorizedException;
import com.ltuc.docconnect.mapper.AppointmentMapper;
import com.ltuc.docconnect.mapper.AvailabilityMapper;
import com.ltuc.docconnect.mapper.ReportMapper;
import com.ltuc.docconnect.repository.AppointmentRepository;
import com.ltuc.docconnect.repository.AvailabilityRepository;
import com.ltuc.docconnect.repository.DoctorRepository;
import com.ltuc.docconnect.service.interfaces.command.DoctorCommandService;
import com.ltuc.docconnect.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorCommandServiceImpl implements DoctorCommandService {

    private final DoctorRepository doctorRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final AvailabilityMapper availabilityMapper;
    private final ReportMapper reportMapper;
    private final AppointmentMapper appointmentMapper;

    @Override
    public AvailabilityResponse addAvailability(AvailabilityRequest request, Authentication authentication, Long doctorId) {

        Doctor doctor = getAuthenticatedDoctor(authentication, doctorId);

        LocalTime minimumEndTime = request.getStartTime().plusMinutes(30);

        if (request.getStartTime().isAfter(request.getEndTime()) ||
                request.getStartTime().equals(request.getEndTime()) ||
                request.getEndTime().isBefore(minimumEndTime)) {
            throw new BusinessLogicException(Messages.INVALID_AVAILABILITY_TIME);
        }

        if (request.getDayOfWeek().equals(LocalDate.now().getDayOfWeek())) {
            if (request.getStartTime().isBefore(LocalTime.now())) {
                throw new BusinessLogicException("Cannot add availability for a time that has already passed today.");
            }
        }

        if (availabilityRepository.existsByDoctorIdAndDayOfWeek(doctor.getId(), request.getDayOfWeek())) {
            throw new BusinessLogicException(Messages.DUPLICATE_AVAILABILITY);
        }

        Availability availability = availabilityMapper.toEntity(request);
        availability.setDoctor(doctor);
        availability.setSlotDuration(30);

        return availabilityMapper.toResponse(availabilityRepository.save(availability));
    }

    @Override
    public AppointmentResponse createReport(Long appointmentId, ReportCreationRequest request, Authentication authentication) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        validateDoctorAccess(appointment, authentication);

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new BusinessLogicException(Messages.CANNOT_REPORT_INVALID_STATUS);
        }

        if (appointment.getReport() != null) {
            throw new BusinessLogicException(Messages.REPORT_ALREADY_EXISTS);
        }

        LocalDateTime appointmentEndTime = LocalDateTime.of(appointment.getDate(), appointment.getEndTime());
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(appointmentEndTime)) {
            throw new BusinessLogicException(Messages.CANNOT_REPORT_BEFORE_TIME);
        }

        if (now.isAfter(appointmentEndTime.plusDays(2))) {
            throw new BusinessLogicException("Cannot create report for an appointment older than 48 hours.");
        }

        Report report = reportMapper.toEntity(request);
        report.setAppointment(appointment);
        appointment.setReport(report);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    private Doctor getAuthenticatedDoctor(Authentication authentication, Long doctorId) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            if (doctorId == null) {
                throw new BadRequestException(Messages.DOCTOR_ID_REQUIRED_FOR_ADMIN);
            }
            return doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));
        }

        Doctor doctor = doctorRepository.findByUserUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "Username", authentication.getName()));

        if (doctorId != null && !doctor.getId().equals(doctorId)) {
            throw new UnauthorizedException(Messages.NOT_AUTHORIZED_APPOINTMENT_OWNERSHIP);
        }

        return doctor;
    }

    private void validateDoctorAccess(Appointment appointment, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !appointment.getDoctor().getUser().getUsername().equals(authentication.getName())) {
            throw new UnauthorizedException(Messages.DOCTOR_UNAUTHORIZED_REPORT);
        }
    }
}