package com.ltuc.docconnect.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Set;

@Entity
@Table(
        name = "specialties",
        indexes = {
                @Index(
                        name = "idx_specialties_name",
                        columnList = "name"
                )
        }
)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Specialty extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(
            mappedBy = "specialty",
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    private Set<DoctorSpecialty> doctorSpecialties;
}