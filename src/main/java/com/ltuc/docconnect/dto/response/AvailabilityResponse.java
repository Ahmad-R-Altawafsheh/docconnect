package com.ltuc.docconnect.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
public class AvailabilityResponse {

    private Long id;
    private Long doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDuration;
}