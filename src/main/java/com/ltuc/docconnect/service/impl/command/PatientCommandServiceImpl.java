package com.ltuc.docconnect.service.impl.command;

import com.ltuc.docconnect.dto.request.AppointmentBookingRequest;
import com.ltuc.docconnect.dto.response.AppointmentResponse;
import com.ltuc.docconnect.dto.response.SlotResponse;
import com.ltuc.docconnect.entity.Appointment;
import com.ltuc.docconnect.entity.Doctor;
import com.ltuc.docconnect.entity.Patient;
import com.ltuc.docconnect.enums.AppointmentStatus;
import com.ltuc.docconnect.exception.BusinessLogicException;
import com.ltuc.docconnect.exception.ResourceNotFoundException;
import com.ltuc.docconnect.exception.UnauthorizedException;
import com.ltuc.docconnect.mapper.AppointmentMapper;
import com.ltuc.docconnect.repository.AppointmentRepository;
import com.ltuc.docconnect.repository.DoctorRepository;
import com.ltuc.docconnect.repository.PatientRepository;
import com.ltuc.docconnect.service.interfaces.command.PatientCommandService;
import com.ltuc.docconnect.service.slot.SlotAvailabilityService;
import com.ltuc.docconnect.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientCommandServiceImpl implements PatientCommandService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;
    private final SlotAvailabilityService slotAvailabilityService;

    @Override
    public AppointmentResponse bookAppointment(AppointmentBookingRequest request, Authentication authentication, Long targetPatientId) {

        Patient patient = getAuthenticatedPatient(authentication, targetPatientId);

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));

        LocalDateTime requestedDateTime = LocalDateTime.of(request.getDate(), request.getStartTime());
        if (requestedDateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessLogicException("Cannot book an appointment in the past.");
        }

        boolean patientHasConflict = appointmentRepository.existsByPatientIdAndDateAndStartTimeAndStatus(
                patient.getId(), request.getDate(), request.getStartTime(), AppointmentStatus.BOOKED);
        if (patientHasConflict) {
            throw new BusinessLogicException("You already have another booking at this time.");
        }

        SlotResponse bookedSlot = slotAvailabilityService.validateAndGetSlot(
                request.getDoctorId(), request.getDate(), request.getStartTime());

        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setFakePaymentTransactionId(UUID.randomUUID().toString());
        appointment.setEndTime(bookedSlot.getEndTime());

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse cancelAppointment(Long appointmentId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (!isAdmin && !appointment.getPatient().getUser().getUsername().equals(authentication.getName())) {
            throw new UnauthorizedException(Messages.NOT_AUTHORIZED_APPOINTMENT_OWNERSHIP);
        }

        LocalDateTime appointmentTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime());
        if (LocalDateTime.now().isAfter(appointmentTime.minusHours(1)) && !isAdmin) {
            throw new BusinessLogicException("Appointments can only be cancelled at least 1 hour before the start time.");
        }

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new BusinessLogicException(Messages.CANNOT_CANCEL_NOT_BOOKED);
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    private Patient getAuthenticatedPatient(Authentication authentication, Long targetPatientId) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin && targetPatientId != null) {
            return patientRepository.findById(targetPatientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", targetPatientId));
        }
        return patientRepository.findByUserUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "Username", authentication.getName()));
    }
}