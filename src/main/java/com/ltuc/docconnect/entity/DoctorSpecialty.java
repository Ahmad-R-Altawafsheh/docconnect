package com.ltuc.docconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "doctor_specialties",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_doctor_specialties_doctor_id_specialty_id",
                        columnNames = {"doctor_id", "specialty_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_doctor_specialties_doctor_id_specialty_id",
                        columnList = "doctor_id, specialty_id"
                ),
                @Index(
                        name = "idx_doctor_specialties_specialty_id",
                        columnList = "specialty_id"
                ),
                @Index(
                        name = "idx_doctor_specialties_doctor_id",
                        columnList = "doctor_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"doctor", "specialty"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class DoctorSpecialty extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    public DoctorSpecialty(Doctor doctor, Specialty specialty) {
        this.doctor = doctor;
        this.specialty = specialty;
    }
}