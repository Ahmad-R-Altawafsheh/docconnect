package com.ltuc.docconnect.mapper;

import com.ltuc.docconnect.dto.response.SpecialtyResponse;
import com.ltuc.docconnect.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappersFacade.class})
public interface SpecialtyMapper {

    SpecialtyMapper INSTANCE = Mappers.getMapper(SpecialtyMapper.class);

    SpecialtyResponse toResponse(Specialty specialty);

    List<SpecialtyResponse> toResponseList(List<Specialty> specialties);
}