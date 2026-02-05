package com.ltuc.docconnect.entity;

import com.ltuc.docconnect.security.entities.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Entity
@Table(
        name = "doctors",
        indexes = {
                @Index(
                        name = "idx_doctors_full_name",
                        columnList = "full_name"
                )
        }
)
@Getter
@Setter
@ToString(exclude = {"doctorSpecialties", "availabilities", "appointments", "user"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Doctor extends BaseEntity {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @OneToMany(
            mappedBy = "doctor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<DoctorSpecialty> doctorSpecialties = new HashSet<>();

    @OneToMany(
            mappedBy = "doctor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Availability> availabilities = new HashSet<>();

    @OneToMany(
            mappedBy = "doctor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Appointment> appointments = new HashSet<>();

    public void addSpecialty(Specialty specialty) {
        if (specialty == null) return;
        DoctorSpecialty doctorSpecialty = new DoctorSpecialty(this, specialty);
        this.doctorSpecialties.add(doctorSpecialty);
    }
}