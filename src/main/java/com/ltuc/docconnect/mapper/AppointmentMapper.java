package com.ltuc.docconnect.mapper;

import com.ltuc.docconnect.dto.request.AppointmentBookingRequest;
import com.ltuc.docconnect.dto.response.AppointmentResponse;
import com.ltuc.docconnect.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {MappersFacade.class})
public interface AppointmentMapper {

    AppointmentMapper INSTANCE = Mappers.getMapper(AppointmentMapper.class);

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.fullName", target = "patientName")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.fullName", target = "doctorName")
    AppointmentResponse toResponse(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "fakePaymentTransactionId", ignore = true)
    @Mapping(target = "report", ignore = true)
    Appointment toEntity(AppointmentBookingRequest request);
}