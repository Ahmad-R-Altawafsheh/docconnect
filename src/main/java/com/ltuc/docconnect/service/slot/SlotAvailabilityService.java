package com.ltuc.docconnect.service.slot;

import com.ltuc.docconnect.dto.response.SlotResponse;
import com.ltuc.docconnect.entity.Appointment;
import com.ltuc.docconnect.entity.Availability;
import com.ltuc.docconnect.exception.BusinessLogicException;
import com.ltuc.docconnect.exception.ResourceNotFoundException;
import com.ltuc.docconnect.repository.AppointmentRepository;
import com.ltuc.docconnect.repository.AvailabilityRepository;
import com.ltuc.docconnect.repository.DoctorRepository;
import com.ltuc.docconnect.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotAvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final SlotCalculationService slotCalculationService;
    private static final int MAX_SEARCH_DAYS = 7;

    public SlotResponse validateAndGetSlot(Long doctorId, LocalDate date, LocalTime startTime) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }

        Availability availabilityRule = getDoctorAvailability(doctorId, date);
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndDate(doctorId, date);

        slotCalculationService.validateSlot(availabilityRule, bookedAppointments, date, startTime);

        return slotCalculationService.calculateFreeSlots(availabilityRule, bookedAppointments, date)
                .stream()
                .filter(slot -> slot.getStartTime().equals(startTime))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(String.format(Messages.SLOT_NOT_AVAILABLE, startTime)));
    }

    public List<SlotResponse> findAllFreeSlots(Long doctorId, LocalDate date) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }
        return getFreeSlotsInternal(doctorId, date);
    }

    public LocalDateTime findNextAvailableSlot(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }

        for (int i = 0; i < MAX_SEARCH_DAYS; i++) {
            LocalDate searchDate = LocalDate.now().plusDays(i);
            List<SlotResponse> availableSlots = getFreeSlotsInternal(doctorId, searchDate);

            if (!availableSlots.isEmpty()) {
                return LocalDateTime.of(searchDate, availableSlots.get(0).getStartTime());
            }
        }
        return null;
    }

    private List<SlotResponse> getFreeSlotsInternal(Long doctorId, LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new BusinessLogicException(Messages.DATE_IN_PAST);
        }

        return availabilityRepository.findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek())
                .map(rule -> {
                    List<Appointment> booked = appointmentRepository.findByDoctorIdAndDate(doctorId, date);
                    return slotCalculationService.calculateFreeSlots(rule, booked, date);
                })
                .orElse(List.of());
    }

    private Availability getDoctorAvailability(Long doctorId, LocalDate date) {
        return availabilityRepository.findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek())
                .orElseThrow(() -> new BusinessLogicException(
                        String.format(Messages.DOCTOR_NOT_AVAILABLE_ON_DAY, date.getDayOfWeek())));
    }
}