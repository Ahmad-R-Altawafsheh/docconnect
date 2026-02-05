package com.ltuc.docconnect.mapper;

import com.ltuc.docconnect.dto.response.PatientResponse;
import com.ltuc.docconnect.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {MappersFacade.class})
public interface PatientMapper {

    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    PatientResponse patientToPatientResponse(Patient patient);
}