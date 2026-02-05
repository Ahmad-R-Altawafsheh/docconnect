package com.ltuc.docconnect.service.slot;

import com.ltuc.docconnect.dto.response.SlotResponse;
import com.ltuc.docconnect.entity.Appointment;
import com.ltuc.docconnect.entity.Availability;
import com.ltuc.docconnect.exception.BusinessLogicException;
import com.ltuc.docconnect.util.Messages;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotCalculationService {

    public void validateSlot(Availability rule, List<Appointment> bookedAppointments, LocalDate date, LocalTime startTime) {
        if (date.isBefore(LocalDate.now())) {
            throw new BusinessLogicException(Messages.DATE_IN_PAST);
        }
        if (date.isEqual(LocalDate.now()) && startTime.isBefore(LocalTime.now())) {
            throw new BusinessLogicException(Messages.APPOINTMENT_TIME_IN_PAST);
        }
        if (startTime.isBefore(rule.getStartTime()) || startTime.isAfter(rule.getEndTime())) {
            throw new BusinessLogicException(String.format(Messages.SLOT_OUTSIDE_WORKING_HOURS, startTime));
        }
        boolean isBooked = bookedAppointments.stream()
                .anyMatch(a -> a.getStartTime().equals(startTime));
        if (isBooked) {
            throw new BusinessLogicException(String.format(Messages.SLOT_ALREADY_BOOKED, startTime));
        }
    }

    public List<SlotResponse> calculateFreeSlots(Availability rule, List<Appointment> bookedAppointments, LocalDate date) {
        int duration = rule.getSlotDuration();
        List<SlotResponse> allPossible = generateAllPossibleSlots(rule.getStartTime(), rule.getEndTime(), duration);

        List<SlotResponse> freeSlots = allPossible.stream()
                .filter(slot -> isSlotFree(slot, bookedAppointments))
                .toList();

        if (date.isEqual(LocalDate.now())) {
            return filterSlotsBeforeNow(freeSlots, LocalTime.now());
        }

        return freeSlots;
    }

    private List<SlotResponse> generateAllPossibleSlots(LocalTime startTime, LocalTime endTime, int duration) {
        List<SlotResponse> slots = new ArrayList<>();
        LocalTime current = startTime;

        while (!current.plusMinutes(duration).isAfter(endTime)) {
            slots.add(SlotResponse.builder()
                    .startTime(current)
                    .endTime(current.plusMinutes(duration))
                    .build());
            current = current.plusMinutes(duration);
        }
        return slots;
    }

    private boolean isSlotFree(SlotResponse slot, List<Appointment> bookedAppointments) {
        return bookedAppointments.stream()
                .noneMatch(booked -> isOverlap(slot.getStartTime(), slot.getEndTime(), booked.getStartTime(), booked.getEndTime()));
    }

    private boolean isOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private List<SlotResponse> filterSlotsBeforeNow(List<SlotResponse> slots, LocalTime currentTime) {
        LocalTime bufferTime = currentTime.plusMinutes(15);

        return slots.stream()
                .filter(slot -> slot.getStartTime().isAfter(bufferTime))
                .toList();
    }
}