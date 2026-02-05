package com.ltuc.docconnect.mapper;

import com.ltuc.docconnect.dto.request.ReportCreationRequest;
import com.ltuc.docconnect.dto.response.ReportResponse;
import com.ltuc.docconnect.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {MappersFacade.class})
public interface ReportMapper {

    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    ReportResponse toResponse(Report report);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Report toEntity(ReportCreationRequest request);
}