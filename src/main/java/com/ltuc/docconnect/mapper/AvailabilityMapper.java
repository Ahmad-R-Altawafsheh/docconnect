package com.ltuc.docconnect.mapper;

import com.ltuc.docconnect.dto.request.AvailabilityRequest;
import com.ltuc.docconnect.dto.response.AvailabilityResponse;
import com.ltuc.docconnect.entity.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {MappersFacade.class})
public interface AvailabilityMapper {

    AvailabilityMapper INSTANCE = Mappers.getMapper(AvailabilityMapper.class);

    @Mapping(source = "doctor.id", target = "doctorId")
    AvailabilityResponse toResponse(Availability availability);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "slotDuration", ignore = true)
    Availability toEntity(AvailabilityRequest request);
}