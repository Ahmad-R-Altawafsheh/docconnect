package com.ltuc.docconnect.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
        name = "availabilities",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_availabilities_doctor_id_day_of_week_start_time",
                        columnNames = {"doctor_id", "day_of_week", "start_time"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_availabilities_doctor_id_day_of_week",
                        columnList = "doctor_id, day_of_week"
                )
        }
)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Availability extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @ToString.Exclude
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_duration_minutes", nullable = false)
    private Integer slotDuration = 30;
}