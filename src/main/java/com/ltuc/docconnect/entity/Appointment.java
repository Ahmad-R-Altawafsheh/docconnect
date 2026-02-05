package com.ltuc.docconnect.entity;

import com.ltuc.docconnect.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "appointments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_appointments_doctor_date_start_time",
                        columnNames = {"doctor_id", "appointment_date", "start_time"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_appointments_patient_id",
                        columnList = "patient_id"
                ),
                @Index(
                        name = "idx_appointments_doctor_id_date",
                        columnList = "doctor_id, appointment_date"
                )
        }
)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @ToString.Exclude
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    @Column(
            name = "payment_transaction_id",
            nullable = false,
            unique = true
    )
    private String fakePaymentTransactionId;

    @OneToOne(
            mappedBy = "appointment",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @ToString.Exclude
    private Report report;
}