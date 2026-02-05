package com.ltuc.docconnect.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Value
@Builder
public class AvailabilityRequest {

    @NotNull(message = "Day of week is required.")
    DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required.")
    LocalTime startTime;

    @NotNull(message = "End time is required.")
    LocalTime endTime;
}