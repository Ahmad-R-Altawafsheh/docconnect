package com.ltuc.docconnect.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
public class AppointmentBookingRequest {

    @NotNull(message = "Doctor ID is required.")
    @Positive(message = "Doctor ID must be a positive number.")
    Long doctorId;

    @NotNull(message = "Appointment date is required.")
    @FutureOrPresent(message = "Appointment date must be today or in the future.")
    LocalDate date;

    @NotNull(message = "Appointment start time is required.")
    LocalTime startTime;
}