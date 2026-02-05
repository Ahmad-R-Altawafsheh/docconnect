package com.ltuc.docconnect.mapper;

import com.ltuc.docconnect.dto.response.DoctorDetailResponse;
import com.ltuc.docconnect.dto.response.SpecialtyResponse;
import com.ltuc.docconnect.entity.Doctor;
import com.ltuc.docconnect.entity.DoctorSpecialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {SpecialtyMapper.class})
public interface DoctorMapper {

    @Mapping(target = "specialties", source = "doctorSpecialties", qualifiedByName = "mapDoctorSpecialtiesToResponses")
    @Mapping(target = "nextAvailableSlot", ignore = true)
    DoctorDetailResponse toDoctorResponse(Doctor doctor);

    @Named("mapDoctorSpecialtiesToResponses")
    default Set<SpecialtyResponse> mapDoctorSpecialtiesToResponses(Set<DoctorSpecialty> doctorSpecialties) {
        if (doctorSpecialties == null || doctorSpecialties.isEmpty()) {
            return Collections.emptySet();
        }

        return doctorSpecialties.stream()
                .map(ds -> SpecialtyResponse.builder()
                        .id(ds.getSpecialty().getId())
                        .name(ds.getSpecialty().getName())
                        .build())
                .collect(Collectors.toSet());
    }
}